package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.AiCoachService
import com.example.data.engine.ProgressionEngine
import com.example.data.local.AppDatabase
import com.example.data.local.AppPreferences
import com.example.data.local.AppSettings
import com.example.data.local.CustomProgramEntity
import com.example.data.local.ExerciseEntity
import com.example.data.local.PersonalRecord
import com.example.data.local.SkillProgress
import com.example.data.local.UserProfileEntity
import com.example.data.local.WorkoutRecord
import com.example.data.model.CalisthenicsData
import com.example.data.model.CoachChatMessage
import com.example.data.model.DayProgram
import com.example.data.model.Exercise
import com.example.data.model.ProposedDay
import com.example.data.model.ProposedProgram
import com.example.data.model.ProgressionInsight
import com.example.data.model.StudyMeReport
import com.example.data.repository.CoachRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ActiveWorkoutState(
    val isActive: Boolean = false,
    val program: DayProgram? = null,
    val currentExerciseIndex: Int = 0,
    val currentSet: Int = 1,
    val isResting: Boolean = false,
    val restSecondsRemaining: Int = 60,
    val restTotalDuration: Int = 60,
    val isRestPaused: Boolean = false,
    val workoutDurationSeconds: Int = 0,
    val completedExercisesCount: Int = 0,
    val isFinished: Boolean = false
) {
    val currentExercise: Exercise?
        get() = program?.exercises?.getOrNull(currentExerciseIndex)

    val totalExercises: Int
        get() = program?.exercises?.size ?: 0

    val isLastSetOfExercise: Boolean
        get() {
            val ex = currentExercise ?: return true
            return currentSet >= ex.targetSets
        }

    val isLastExercise: Boolean
        get() {
            val total = program?.exercises?.size ?: 0
            return currentExerciseIndex >= total - 1
        }
}

class CoachViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val preferences = AppPreferences(application)
    val repository = CoachRepository(database, preferences)
    private val aiCoachService = AiCoachService()

    val settings: StateFlow<AppSettings> = repository.appSettings

    val workoutRecords: StateFlow<List<WorkoutRecord>> = repository.allWorkoutRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val personalRecords: StateFlow<List<PersonalRecord>> = repository.allPRs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val skillProgressList: StateFlow<List<SkillProgress>> = repository.allSkills
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allExercises: StateFlow<List<ExerciseEntity>> = repository.allExercises
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPrograms: StateFlow<List<CustomProgramEntity>> = repository.allPrograms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeProgram: StateFlow<CustomProgramEntity?> = repository.activeProgram
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeProgramDays: StateFlow<List<com.example.data.local.RoutineDayEntity>> = activeProgram
        .flatMapLatest { prog ->
            if (prog != null) {
                repository.getDaysForProgram(prog.id)
            } else {
                flowOf(emptyList())
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Calculated streak from workout records
    val workoutStreak: StateFlow<Int> = workoutRecords.combine(settings) { records, _ ->
        repository.calculateStreak(records)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Rules-Based Progression Engine Insights
    val progressionInsights: StateFlow<List<ProgressionInsight>> = combine(
        userProfile,
        workoutRecords,
        personalRecords,
        skillProgressList
    ) { profile, records, prs, skills ->
        ProgressionEngine.analyzeProgress(profile, records, prs, skills)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // KINETIX Gamification Rank & XP Progression
    val rankProgression: StateFlow<com.example.data.model.RankProgressData> = combine(
        workoutRecords,
        skillProgressList,
        personalRecords,
        workoutStreak
    ) { records, skills, prs, streak ->
        com.example.data.model.RankCalculator.calculateRank(
            workoutCount = records.size,
            skillLevelsSum = skills.sumOf { it.currentLevel },
            prCount = prs.size,
            streakDays = streak
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        com.example.data.model.RankCalculator.calculateRank(0, 0, 0, 0)
    )

    // Community Feed State
    val communityPosts: StateFlow<List<com.example.data.local.CommunityPostEntity>> = repository.allCommunityPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 345+ Exercise Library Filtering & Search
    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedEquipment = MutableStateFlow("All")
    val selectedEquipment: StateFlow<String> = _selectedEquipment.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredExercises: StateFlow<List<ExerciseEntity>> = combine(
        allExercises,
        _selectedCategory,
        _selectedEquipment,
        _searchQuery
    ) { list, cat, equip, query ->
        list.filter { ex ->
            val matchesCat = cat == "All" || ex.category.equals(cat, ignoreCase = true)
            val matchesEquip = equip == "All" || ex.equipmentRequired.contains(equip, ignoreCase = true)
            val matchesQuery = query.isBlank() || ex.name.contains(query, ignoreCase = true) ||
                    ex.muscleGroup.contains(query, ignoreCase = true) ||
                    ex.equipmentRequired.contains(query, ignoreCase = true)
            matchesCat && matchesEquip && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Onboarding & Multi-Provider Auth State
    private val _onboardingStep = MutableStateFlow(0) // 0 = not in onboarding or completed
    val onboardingStep: StateFlow<Int> = _onboardingStep.asStateFlow()

    private val _authProvider = MutableStateFlow("Google (crmyhsk@gmail.com)")
    val authProvider: StateFlow<String> = _authProvider.asStateFlow()

    private val _userAvatar = MutableStateFlow("falcon")
    val userAvatar: StateFlow<String> = _userAvatar.asStateFlow()

    // "Study Me" Comprehensive Diagnosis
    val studyMeReport: StateFlow<StudyMeReport> = combine(
        userProfile,
        workoutRecords,
        personalRecords,
        skillProgressList,
        workoutStreak
    ) { profile, records, prs, skills, streak ->
        ProgressionEngine.generateStudyMeReport(profile, records, prs, skills, streak)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        StudyMeReport(80, 0, 0, "Initializing", "Foundation", emptyList(), emptyList(), "Phase 1")
    )

    // AI Coach Chat State
    private val _aiCoachMessages = MutableStateFlow<List<CoachChatMessage>>(
        listOf(
            CoachChatMessage(
                sender = "coach",
                text = "Welcome to your Personal Adaptive Coach. I maintain your long-term training memory, analyze your PRs, and build structured multi-week programs. How can I adapt your training today?"
            )
        )
    )
    val aiCoachMessages: StateFlow<List<CoachChatMessage>> = _aiCoachMessages.asStateFlow()

    private val _isAiCoachLoading = MutableStateFlow(false)
    val isAiCoachLoading: StateFlow<Boolean> = _isAiCoachLoading.asStateFlow()

    // Active Workout state
    private val _activeWorkout = MutableStateFlow(ActiveWorkoutState())
    val activeWorkout: StateFlow<ActiveWorkoutState> = _activeWorkout.asStateFlow()

    // Navigation tab state: "home", "program", "skills", "coach", "profile"
    private val _currentTab = MutableStateFlow("home")
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    // Selected skill for detail/progression view: "handstand", "lsit", "planche", "front_lever", "muscle_up"
    private val _selectedSkillId = MutableStateFlow("handstand")
    val selectedSkillId: StateFlow<String> = _selectedSkillId.asStateFlow()

    // Active timers
    private var workoutTimerJob: Job? = null
    private var restTimerJob: Job? = null

    fun selectTab(tab: String) {
        _currentTab.value = tab
    }

    fun selectSkill(skillId: String) {
        _selectedSkillId.value = skillId
    }

    fun getTodayProgram(): DayProgram {
        val days = activeProgramDays.value
        val todayName = repository.getTodayDayName()
        if (days.isNotEmpty()) {
            val matchingDay = days.find { it.dayName.equals(todayName, ignoreCase = true) }
            if (matchingDay != null) {
                return CoachRepository.routineDayToDayProgram(matchingDay)
            }
        }
        return repository.getTodayProgram()
    }
    fun getTodayDayName(): String = repository.getTodayDayName()
    fun getNextRestDay(): String = repository.getNextRestDay()

    // AI Coach interaction
    fun sendCoachPrompt(userPrompt: String) {
        if (userPrompt.isBlank()) return
        val userMsg = CoachChatMessage(sender = "user", text = userPrompt.trim())
        _aiCoachMessages.value = _aiCoachMessages.value + userMsg
        _isAiCoachLoading.value = true

        viewModelScope.launch {
            val (coachText, proposedProgram) = aiCoachService.generateCoachResponse(
                userPrompt = userPrompt,
                profile = userProfile.value,
                prs = personalRecords.value,
                skills = skillProgressList.value,
                records = workoutRecords.value
            )

            val coachMsg = CoachChatMessage(
                sender = "coach",
                text = coachText,
                proposal = proposedProgram
            )
            _aiCoachMessages.value = _aiCoachMessages.value + coachMsg
            _isAiCoachLoading.value = false
        }
    }

    fun acceptProposedProgram(proposed: ProposedProgram) {
        viewModelScope.launch {
            repository.applyProposedProgram(proposed)
            val confirmMsg = CoachChatMessage(
                sender = "coach",
                text = "✓ Program '${proposed.title}' has been accepted and set as your active ${proposed.totalWeeks}-week schedule in local storage! You can review or customize it anytime in the Program tab."
            )
            _aiCoachMessages.value = _aiCoachMessages.value + confirmMsg
        }
    }

    fun updateUserProfile(
        fitnessLevel: String,
        experienceYears: String,
        primaryGoal: String,
        secondaryGoal: String,
        equipment: String,
        daysPerWeek: Int,
        durationMinutes: Int,
        limitations: String
    ) {
        viewModelScope.launch {
            repository.updateUserProfileFields(
                fitnessLevel = fitnessLevel,
                experienceYears = experienceYears,
                primaryGoal = primaryGoal,
                secondaryGoal = secondaryGoal,
                equipment = equipment,
                daysPerWeek = daysPerWeek,
                durationMinutes = durationMinutes,
                limitations = limitations
            )
        }
    }

    fun setExerciseCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setEquipmentFilter(equipment: String) {
        _selectedEquipment.value = equipment
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setOnboardingStep(step: Int) {
        _onboardingStep.value = step
    }

    fun setAuthProvider(provider: String) {
        _authProvider.value = provider
    }

    fun setUserAvatar(avatar: String) {
        _userAvatar.value = avatar
    }

    fun completeOnboarding(
        fitnessLevel: String,
        primaryGoal: String,
        equipment: String,
        daysPerWeek: Int,
        weightKg: Float,
        heightCm: Float
    ) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileEntity()
            val updated = current.copy(
                fitnessLevel = fitnessLevel,
                primaryGoal = primaryGoal,
                equipmentAvailable = equipment,
                trainingDaysPerWeek = daysPerWeek,
                weightKg = weightKg,
                heightCm = heightCm,
                isOnboardingCompleted = true,
                avatarId = _userAvatar.value,
                authProvider = _authProvider.value
            )
            repository.saveUserProfile(updated)
            _onboardingStep.value = 0
        }
    }

    fun toggleLikeCommunityPost(postId: String, currentLikes: Int, isLiked: Boolean) {
        viewModelScope.launch {
            repository.toggleLikePost(postId, currentLikes, isLiked)
        }
    }

    fun toggleBookmarkCommunityPost(postId: String, isBookmarked: Boolean) {
        viewModelScope.launch {
            repository.toggleBookmarkPost(postId, isBookmarked)
        }
    }

    fun publishCommunityPost(content: String, workoutTag: String, prTag: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            val user = userProfile.value
            val rank = "${rankProgression.value.currentTier.tierName} ${rankProgression.value.currentTier.division}"
            val post = com.example.data.local.CommunityPostEntity(
                id = "post_" + java.util.UUID.randomUUID().toString().take(8),
                authorName = user?.displayName ?: "Kinetix Athlete",
                authorHandle = "@" + (user?.username ?: "apex_athlete"),
                authorRankTier = rank,
                avatarId = _userAvatar.value,
                timeAgo = "Just now",
                content = content.trim(),
                workoutTag = workoutTag,
                prTag = prTag,
                likesCount = 0,
                isLiked = false,
                isBookmarked = false,
                commentsCount = 0
            )
            repository.insertCommunityPost(post)
        }
    }

    fun deleteCommunityPost(postId: String) {
        viewModelScope.launch {
            repository.deleteCommunityPost(postId)
        }
    }

    fun deletePR(recordKey: String) {
        viewModelScope.launch {
            repository.deletePR(recordKey)
        }
    }

    fun deleteWorkoutRecord(recordId: Long) {
        viewModelScope.launch {
            repository.deleteWorkoutRecord(recordId)
        }
    }

    fun setActiveProgram(programId: String) {
        viewModelScope.launch {
            repository.setActiveProgram(programId)
        }
    }

    fun saveCustomExercise(exercise: ExerciseEntity) {
        viewModelScope.launch {
            repository.saveCustomExercise(exercise)
        }
    }

    fun deleteExercise(id: String) {
        viewModelScope.launch {
            repository.deleteExercise(id)
        }
    }

    fun startWorkout(program: DayProgram) {
        if (program.exercises.isEmpty()) return
        restTimerJob?.cancel()
        workoutTimerJob?.cancel()

        val defaultRest = settings.value.restTimeSeconds
        _activeWorkout.value = ActiveWorkoutState(
            isActive = true,
            program = program,
            currentExerciseIndex = 0,
            currentSet = 1,
            isResting = false,
            restSecondsRemaining = defaultRest,
            restTotalDuration = defaultRest,
            isRestPaused = false,
            workoutDurationSeconds = 0,
            completedExercisesCount = 0,
            isFinished = false
        )

        startWorkoutDurationTimer()
    }

    private fun startWorkoutDurationTimer() {
        workoutTimerJob?.cancel()
        workoutTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _activeWorkout.value = _activeWorkout.value.copy(
                    workoutDurationSeconds = _activeWorkout.value.workoutDurationSeconds + 1
                )
            }
        }
    }

    fun completeCurrentSet(context: Context) {
        val current = _activeWorkout.value
        val ex = current.currentExercise ?: return

        vibrateShort(context)

        if (current.currentSet < ex.targetSets) {
            // Move to next set, trigger rest timer
            val defaultRest = settings.value.restTimeSeconds
            _activeWorkout.value = current.copy(
                currentSet = current.currentSet + 1,
                isResting = true,
                restSecondsRemaining = defaultRest,
                restTotalDuration = defaultRest,
                isRestPaused = false
            )
            startRestTimer(context)
        } else {
            // Exercise completed
            val updatedCompletedCount = current.completedExercisesCount + 1
            if (current.isLastExercise) {
                // Workout Finished!
                finishActiveWorkout(context, updatedCompletedCount)
            } else {
                // Next Exercise
                val defaultRest = settings.value.restTimeSeconds
                _activeWorkout.value = current.copy(
                    currentExerciseIndex = current.currentExerciseIndex + 1,
                    currentSet = 1,
                    completedExercisesCount = updatedCompletedCount,
                    isResting = true,
                    restSecondsRemaining = defaultRest,
                    restTotalDuration = defaultRest,
                    isRestPaused = false
                )
                startRestTimer(context)
            }
        }
    }

    private fun startRestTimer(context: Context) {
        restTimerJob?.cancel()
        restTimerJob = viewModelScope.launch {
            while (_activeWorkout.value.isResting && _activeWorkout.value.restSecondsRemaining > 0) {
                delay(1000)
                if (!_activeWorkout.value.isRestPaused) {
                    val remaining = _activeWorkout.value.restSecondsRemaining - 1
                    _activeWorkout.value = _activeWorkout.value.copy(restSecondsRemaining = remaining)
                    if (remaining <= 0) {
                        // Rest finished
                        vibrateRestFinished(context)
                        _activeWorkout.value = _activeWorkout.value.copy(isResting = false)
                        break
                    }
                }
            }
        }
    }

    fun togglePauseRest() {
        val current = _activeWorkout.value
        _activeWorkout.value = current.copy(isRestPaused = !current.isRestPaused)
    }

    fun addRestTime(seconds: Int = 30) {
        val current = _activeWorkout.value
        val newRemaining = current.restSecondsRemaining + seconds
        val newTotal = maxOf(current.restTotalDuration, newRemaining)
        _activeWorkout.value = current.copy(
            restSecondsRemaining = newRemaining,
            restTotalDuration = newTotal
        )
    }

    fun skipRest() {
        restTimerJob?.cancel()
        _activeWorkout.value = _activeWorkout.value.copy(isResting = false)
    }

    fun skipExercise() {
        restTimerJob?.cancel()
        val current = _activeWorkout.value
        if (current.isLastExercise) {
            finishActiveWorkout(getApplication(), current.completedExercisesCount)
        } else {
            _activeWorkout.value = current.copy(
                currentExerciseIndex = current.currentExerciseIndex + 1,
                currentSet = 1,
                isResting = false
            )
        }
    }

    fun previousExercise() {
        restTimerJob?.cancel()
        val current = _activeWorkout.value
        if (current.currentExerciseIndex > 0) {
            _activeWorkout.value = current.copy(
                currentExerciseIndex = current.currentExerciseIndex - 1,
                currentSet = 1,
                isResting = false
            )
        }
    }

    private fun finishActiveWorkout(context: Context, completedCount: Int) {
        restTimerJob?.cancel()
        workoutTimerJob?.cancel()
        val current = _activeWorkout.value
        val program = current.program ?: return

        vibrateCelebration(context)

        _activeWorkout.value = current.copy(
            completedExercisesCount = completedCount,
            isResting = false,
            isFinished = true
        )

        viewModelScope.launch {
            repository.saveCompletedWorkout(
                workoutName = program.workoutName,
                dayOfWeek = program.dayName,
                durationSeconds = current.workoutDurationSeconds,
                completedCount = completedCount,
                totalCount = program.exercises.size
            )
        }
    }

    fun exitWorkout() {
        restTimerJob?.cancel()
        workoutTimerJob?.cancel()
        _activeWorkout.value = ActiveWorkoutState(isActive = false)
    }

    // Skills actions
    fun setSkillLevel(skillId: String, level: Int) {
        viewModelScope.launch {
            repository.updateSkillLevel(skillId, level)
        }
    }

    fun updateSkillHoldTime(skillId: String, holdSeconds: Int) {
        viewModelScope.launch {
            repository.updateSkillHold(skillId, holdSeconds)
        }
    }

    // PR actions
    fun savePR(key: String, name: String, value: Int, unit: String, category: String) {
        viewModelScope.launch {
            repository.updatePR(key, name, value, unit, category)
        }
    }

    // Settings actions
    fun setRestDuration(seconds: Int) = repository.setRestTime(seconds)
    fun setProgramWeek(week: Int) = repository.setCurrentWeek(week)
    fun setRemindersEnabled(enabled: Boolean) = repository.setRemindersEnabled(enabled)
    fun setReminderTime(time: String) = repository.setReminderTime(time)
    fun setAppTheme(theme: String) = repository.setAppTheme(theme)

    fun resetAllData() {
        viewModelScope.launch {
            repository.resetAllProgress()
            exitWorkout()
        }
    }

    private fun vibrateShort(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrate(50)
            }
        } catch (_: Exception) {}
    }

    private fun vibrateRestFinished(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                    vm?.defaultVibrator
                } else {
                    @Suppress("DEPRECATION")
                    context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                }
                vibrator?.vibrate(
                    VibrationEffect.createWaveform(longArrayOf(0, 200, 100, 200), -1)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrate(300)
            }
        } catch (_: Exception) {}
    }

    private fun vibrateCelebration(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                    vm?.defaultVibrator
                } else {
                    @Suppress("DEPRECATION")
                    context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                }
                vibrator?.vibrate(
                    VibrationEffect.createWaveform(longArrayOf(0, 150, 80, 150, 80, 250), -1)
                )
            }
        } catch (_: Exception) {}
    }
}
