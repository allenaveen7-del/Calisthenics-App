package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.AppPreferences
import com.example.data.local.AppSettings
import com.example.data.local.CustomProgramEntity
import com.example.data.local.ExerciseEntity
import com.example.data.local.PersonalRecord
import com.example.data.local.RoutineDayEntity
import com.example.data.local.SkillProgress
import com.example.data.local.UserProfileEntity
import com.example.data.local.WorkoutRecord
import com.example.data.model.CalisthenicsData
import com.example.data.model.CalisthenicsLibrary
import com.example.data.model.DayProgram
import com.example.data.model.Exercise
import com.example.data.model.ProposedProgram
import com.example.data.model.RoutineExercise
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit

class CoachRepository(
    private val database: AppDatabase,
    private val preferences: AppPreferences
) {
    private val workoutDao = database.workoutRecordDao()
    private val prDao = database.personalRecordDao()
    private val skillDao = database.skillProgressDao()
    private val profileDao = database.userProfileDao()
    private val exerciseDao = database.exerciseDao()
    private val programDao = database.programDao()
    private val communityDao = database.communityPostDao()

    val allWorkoutRecords: Flow<List<WorkoutRecord>> = workoutDao.getAllRecords()
    val allPRs: Flow<List<PersonalRecord>> = prDao.getAllPRs()
    val allSkills: Flow<List<SkillProgress>> = skillDao.getAllSkillProgress()
    val appSettings: StateFlow<AppSettings> = preferences.settingsFlow

    val userProfile: Flow<UserProfileEntity?> = profileDao.getUserProfile()
    val allExercises: Flow<List<ExerciseEntity>> = exerciseDao.getAllExercises()
    val allPrograms: Flow<List<CustomProgramEntity>> = programDao.getAllPrograms()
    val activeProgram: Flow<CustomProgramEntity?> = programDao.getActiveProgram()
    val allCommunityPosts: Flow<List<com.example.data.local.CommunityPostEntity>> = communityDao.getAllPosts()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedDefaultsIfNeeded()
        }
    }

    private suspend fun seedDefaultsIfNeeded() {
        // 1. Seed PRs
        val existingPRs = prDao.getAllPRs().first()
        val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        val todayStr = dateFormat.format(Date())
        val missingPRs = CalisthenicsData.DefaultPersonalRecords.filter { def ->
            existingPRs.none { it.recordKey == def.key }
        }.map {
            PersonalRecord(
                recordKey = it.key,
                exerciseName = it.name,
                recordValue = it.defaultValue,
                unit = it.unit,
                category = it.category,
                dateAchieved = todayStr
            )
        }
        if (missingPRs.isNotEmpty()) {
            prDao.insertAll(missingPRs)
        }

        // 2. Seed Skills
        val existingSkills = skillDao.getAllSkillProgress().first()
        val missingSkills = CalisthenicsData.SkillProgressions.filter { prog ->
            existingSkills.none { it.skillId == prog.id }
        }.map {
            SkillProgress(
                skillId = it.id,
                skillName = it.name,
                currentLevel = 1,
                maxLevel = it.levels.size,
                notes = "Starting progression",
                bestHoldSeconds = 0
            )
        }
        if (missingSkills.isNotEmpty()) {
            skillDao.insertAll(missingSkills)
        }

        // 3. Seed User Profile
        val existingProfile = profileDao.getUserProfileSync()
        if (existingProfile == null) {
            val defaultProfile = UserProfileEntity(
                id = 1,
                fitnessLevel = "Intermediate",
                experienceYears = "1-2 years",
                primaryGoal = "Skill Mastery & Relative Strength",
                secondaryGoal = "Muscle-up & Planche Mastery",
                equipmentAvailable = "Pull-up Bar, Dip Station, Floor, Rings",
                trainingDaysPerWeek = 5,
                workoutDurationMinutes = 45,
                jointLimitations = "None",
                currentProgramPhase = "Phase 1: Athletic Foundation & Hypertrophy",
                currentWeek = 1,
                totalWeeksInPhase = 8,
                notes = "Personal calisthenics training profile initialized."
            )
            profileDao.insertOrUpdateProfile(defaultProfile)
        }

        // 4. Seed Exercise Database
        val existingExercises = exerciseDao.getAllExercises().first()
        if (existingExercises.size < CalisthenicsLibrary.DefaultExercises.size) {
            exerciseDao.insertAll(CalisthenicsLibrary.DefaultExercises)
        }

        // 5. Seed Initial Program & Days
        val activeProg = programDao.getActiveProgramSync()
        if (activeProg == null) {
            val initialProgramId = "prog_phase_1"
            val initialProgram = CustomProgramEntity(
                id = initialProgramId,
                name = "Phase 1: Athletic Foundation & Hypertrophy",
                description = "Classic 5-day calisthenics split targeting horizontal and vertical pushing/pulling, core compression, and skill foundations.",
                totalWeeks = 8,
                currentWeek = 1,
                isActive = true
            )
            programDao.insertProgram(initialProgram)

            val routineDays = CalisthenicsData.WeeklySchedule.map { dayProg ->
                val exercisesList = dayProg.exercises.map { ex ->
                    RoutineExercise(
                        id = ex.id,
                        name = ex.name,
                        targetSets = ex.targetSets,
                        targetRepsOrDuration = ex.targetRepsOrDuration,
                        shortInstructions = ex.shortInstructions,
                        formCues = ex.formCues,
                        muscleGroup = ex.muscleGroup,
                        isDurationBased = ex.isDurationBased,
                        defaultDurationSeconds = ex.defaultDurationSeconds
                    )
                }

                RoutineDayEntity(
                    programId = initialProgramId,
                    dayName = dayProg.dayName,
                    workoutName = dayProg.workoutName,
                    isRestDay = dayProg.isRestDay,
                    focusDescription = dayProg.focusDescription,
                    exercisesJson = routineExercisesToJson(exercisesList)
                )
            }
            programDao.insertRoutineDays(routineDays)
        }

        // 6. Seed Initial Community Posts
        val existingPosts = communityDao.getAllPosts().first()
        if (existingPosts.isEmpty()) {
            val seedPosts = listOf(
                com.example.data.local.CommunityPostEntity(
                    id = "post_1",
                    authorName = "Leonidas Vance",
                    authorHandle = "@vance_cali",
                    authorRankTier = "Grandmaster",
                    avatarId = "falcon",
                    timeAgo = "2h ago",
                    content = "After 6 months of protraction drills, planche leans, and high-frequency parallette volume, finally locked 10 strict seconds with locked elbows. Trust the kinetic progression!",
                    workoutTag = "PARALLETTES STATICS",
                    prTag = "Full Planche 10s",
                    likesCount = 84,
                    isLiked = false,
                    isBookmarked = false,
                    commentsCount = 19
                ),
                com.example.data.local.CommunityPostEntity(
                    id = "post_2",
                    authorName = "Aria Sterling",
                    authorHandle = "@aria_titan",
                    authorRankTier = "Titan Conqueror",
                    avatarId = "iron",
                    timeAgo = "4h ago",
                    content = "Combining gymnastics ring false-grip volume with heavy barbell posterior chain work is the ultimate formula for tendon resilience and dense hypertrophy.",
                    workoutTag = "HYBRID IRON & RINGS",
                    prTag = "180kg Deadlift + Muscle-up",
                    likesCount = 142,
                    isLiked = true,
                    isBookmarked = true,
                    commentsCount = 37
                ),
                com.example.data.local.CommunityPostEntity(
                    id = "post_3",
                    authorName = "Marcus Thorne",
                    authorHandle = "@thorne_apex",
                    authorRankTier = "Master Tier",
                    avatarId = "kinetic",
                    timeAgo = "Yesterday",
                    content = "Banded counterweight drops to 5kg this morning! Forearm flexors and lat engagement feeling like solid steel. Keep climbing the Kinetix tiers.",
                    workoutTag = "RELATIVE STRENGTH",
                    prTag = "OAC Negatives (5s)",
                    likesCount = 67,
                    isLiked = false,
                    isBookmarked = false,
                    commentsCount = 12
                )
            )
            communityDao.insertAll(seedPosts)
        }
    }

    suspend fun saveUserProfile(profile: UserProfileEntity) {
        profileDao.insertOrUpdateProfile(profile)
    }

    suspend fun updateUserProfileFields(
        fitnessLevel: String,
        experienceYears: String,
        primaryGoal: String,
        secondaryGoal: String,
        equipment: String,
        daysPerWeek: Int,
        durationMinutes: Int,
        limitations: String
    ) {
        val current = profileDao.getUserProfileSync() ?: UserProfileEntity()
        val updated = current.copy(
            fitnessLevel = fitnessLevel,
            experienceYears = experienceYears,
            primaryGoal = primaryGoal,
            secondaryGoal = secondaryGoal,
            equipmentAvailable = equipment,
            trainingDaysPerWeek = daysPerWeek,
            workoutDurationMinutes = durationMinutes,
            jointLimitations = limitations
        )
        profileDao.insertOrUpdateProfile(updated)
    }

    suspend fun updateProgramPhase(phase: String, currentWeek: Int, totalWeeks: Int) {
        profileDao.updateProgramPhase(phase, currentWeek, totalWeeks)
    }

    fun getDaysForProgram(programId: String): Flow<List<RoutineDayEntity>> {
        return programDao.getDaysForProgram(programId)
    }

    suspend fun getDaysForProgramSync(programId: String): List<RoutineDayEntity> {
        return programDao.getDaysForProgramSync(programId)
    }

    suspend fun applyProposedProgram(proposed: ProposedProgram): String {
        val programId = "prog_" + UUID.randomUUID().toString().take(8)
        val entity = CustomProgramEntity(
            id = programId,
            name = proposed.title,
            description = proposed.description,
            totalWeeks = proposed.totalWeeks,
            currentWeek = 1,
            isActive = true
        )
        programDao.insertProgram(entity)
        programDao.setActiveProgram(programId)

        val days = proposed.days.map { day ->
            RoutineDayEntity(
                programId = programId,
                dayName = day.dayName,
                workoutName = day.workoutName,
                isRestDay = day.isRestDay,
                focusDescription = day.focusDescription,
                exercisesJson = routineExercisesToJson(day.exercises)
            )
        }
        programDao.insertRoutineDays(days)

        // Update profile current phase
        profileDao.updateProgramPhase(proposed.title, 1, proposed.totalWeeks)
        return programId
    }

    suspend fun setActiveProgram(programId: String) {
        programDao.setActiveProgram(programId)
        val prog = programDao.getProgramById(programId)
        if (prog != null) {
            profileDao.updateProgramPhase(prog.name, prog.currentWeek, prog.totalWeeks)
        }
    }

    suspend fun saveCustomExercise(exercise: ExerciseEntity) {
        exerciseDao.insertExercise(exercise)
    }

    suspend fun deleteExercise(id: String) {
        exerciseDao.deleteExerciseById(id)
    }

    suspend fun saveCompletedWorkout(
        workoutName: String,
        dayOfWeek: String,
        durationSeconds: Int,
        completedCount: Int,
        totalCount: Int
    ): Long {
        val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        val record = WorkoutRecord(
            workoutName = workoutName,
            dayOfWeek = dayOfWeek,
            timestamp = System.currentTimeMillis(),
            durationSeconds = durationSeconds,
            completedExercisesCount = completedCount,
            totalExercisesCount = totalCount,
            dateFormatted = dateFormat.format(Date())
        )
        return workoutDao.insertRecord(record)
    }

    suspend fun updateSkillLevel(skillId: String, level: Int) {
        skillDao.updateLevel(skillId, level.coerceIn(1, 5))
    }

    suspend fun updateSkillHold(skillId: String, holdSeconds: Int) {
        skillDao.updateBestHold(skillId, holdSeconds)
    }

    suspend fun updatePR(recordKey: String, name: String, value: Int, unit: String, category: String) {
        val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        prDao.insertOrUpdatePR(
            PersonalRecord(
                recordKey = recordKey,
                exerciseName = name,
                recordValue = value,
                unit = unit,
                category = category,
                dateAchieved = dateFormat.format(Date())
            )
        )
    }

    suspend fun deletePR(recordKey: String) {
        prDao.deletePR(recordKey)
    }

    suspend fun deleteWorkoutRecord(recordId: Long) {
        workoutDao.deleteRecordById(recordId)
    }

    suspend fun insertCommunityPost(post: com.example.data.local.CommunityPostEntity) {
        communityDao.insertPost(post)
    }

    suspend fun toggleLikePost(postId: String, currentLikes: Int, isLiked: Boolean) {
        val newLikes = if (isLiked) (currentLikes - 1).coerceAtLeast(0) else currentLikes + 1
        communityDao.updateLike(postId, !isLiked, if (isLiked) -1 else 1)
    }

    suspend fun toggleBookmarkPost(postId: String, isBookmarked: Boolean) {
        communityDao.updateBookmark(postId, !isBookmarked)
    }

    suspend fun deleteCommunityPost(postId: String) {
        communityDao.deletePostById(postId)
    }

    fun setRestTime(seconds: Int) = preferences.setRestTime(seconds)
    fun setCurrentWeek(week: Int) {
        preferences.setCurrentWeek(week)
        CoroutineScope(Dispatchers.IO).launch {
            val prof = profileDao.getUserProfileSync()
            if (prof != null) {
                profileDao.updateProgramPhase(prof.currentProgramPhase, week, prof.totalWeeksInPhase)
            }
        }
    }
    fun setRemindersEnabled(enabled: Boolean) = preferences.setRemindersEnabled(enabled)
    fun setReminderTime(time: String) = preferences.setReminderTime(time)
    fun setAppTheme(theme: String) = preferences.setAppTheme(theme)

    suspend fun resetAllProgress() {
        workoutDao.clearAll()
        prDao.clearAll()
        skillDao.clearAll()
        exerciseDao.clearAll()
        programDao.clearAllPrograms()
        programDao.clearAllDays()
        profileDao.clearAll()
        preferences.resetSettings()
        seedDefaultsIfNeeded()
    }

    fun getTodayDayName(): String {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Monday"
            Calendar.TUESDAY -> "Tuesday"
            Calendar.WEDNESDAY -> "Wednesday"
            Calendar.THURSDAY -> "Thursday"
            Calendar.FRIDAY -> "Friday"
            Calendar.SATURDAY -> "Saturday"
            Calendar.SUNDAY -> "Sunday"
            else -> "Monday"
        }
    }

    suspend fun getTodayProgramFromDb(): DayProgram {
        val todayName = getTodayDayName()
        val activeProg = programDao.getActiveProgramSync()
        if (activeProg != null) {
            val days = programDao.getDaysForProgramSync(activeProg.id)
            val todayDay = days.find { it.dayName.equals(todayName, ignoreCase = true) }
            if (todayDay != null) {
                return routineDayToDayProgram(todayDay)
            }
        }
        return CalisthenicsData.WeeklySchedule.find { it.dayName.equals(todayName, ignoreCase = true) }
            ?: CalisthenicsData.WeeklySchedule[0]
    }

    fun getTodayProgram(): DayProgram {
        val todayName = getTodayDayName()
        return CalisthenicsData.WeeklySchedule.find { it.dayName.equals(todayName, ignoreCase = true) }
            ?: CalisthenicsData.WeeklySchedule[0]
    }

    fun getNextRestDay(): String {
        val todayName = getTodayDayName()
        return when (todayName) {
            "Monday", "Tuesday" -> "Wednesday"
            "Wednesday" -> "Today is Rest Day (Next: Sunday)"
            "Thursday", "Friday", "Saturday" -> "Sunday"
            "Sunday" -> "Today is Rest Day (Next: Wednesday)"
            else -> "Wednesday"
        }
    }

    fun calculateStreak(records: List<WorkoutRecord>): Int {
        if (records.isEmpty()) return 0

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val todayStart = calendar.timeInMillis
        val oneDayMillis = TimeUnit.DAYS.toMillis(1)

        val uniqueDayTimestamps = records
            .map { record ->
                val c = Calendar.getInstance().apply { timeInMillis = record.timestamp }
                c.set(Calendar.HOUR_OF_DAY, 0)
                c.set(Calendar.MINUTE, 0)
                c.set(Calendar.SECOND, 0)
                c.set(Calendar.MILLISECOND, 0)
                c.timeInMillis
            }
            .distinct()
            .sortedDescending()

        if (uniqueDayTimestamps.isEmpty()) return 0

        val mostRecent = uniqueDayTimestamps.first()
        val diffFromToday = (todayStart - mostRecent) / oneDayMillis

        if (diffFromToday > 1) {
            return 0
        }

        var streak = 1
        for (i in 0 until uniqueDayTimestamps.size - 1) {
            val current = uniqueDayTimestamps[i]
            val prev = uniqueDayTimestamps[i + 1]
            val dayDiff = (current - prev) / oneDayMillis
            if (dayDiff == 1L) {
                streak++
            } else if (dayDiff == 2L) {
                val checkCal = Calendar.getInstance().apply { timeInMillis = prev + oneDayMillis }
                val dayOfWeek = checkCal.get(Calendar.DAY_OF_WEEK)
                if (dayOfWeek == Calendar.WEDNESDAY || dayOfWeek == Calendar.SUNDAY) {
                    streak++
                } else {
                    break
                }
            } else {
                break
            }
        }
        return streak
    }

    companion object {
        fun routineExercisesToJson(exercises: List<RoutineExercise>): String {
            val array = JSONArray()
            for (ex in exercises) {
                val obj = JSONObject().apply {
                    put("id", ex.id)
                    put("name", ex.name)
                    put("targetSets", ex.targetSets)
                    put("targetRepsOrDuration", ex.targetRepsOrDuration)
                    put("shortInstructions", ex.shortInstructions)
                    put("muscleGroup", ex.muscleGroup)
                    put("isDurationBased", ex.isDurationBased)
                    put("defaultDurationSeconds", ex.defaultDurationSeconds)
                    val cuesArr = JSONArray()
                    ex.formCues.forEach { cuesArr.put(it) }
                    put("formCues", cuesArr)
                }
                array.put(obj)
            }
            return array.toString()
        }

        fun jsonToRoutineExercises(json: String): List<RoutineExercise> {
            if (json.isBlank()) return emptyList()
            val list = mutableListOf<RoutineExercise>()
            try {
                val array = JSONArray(json)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val cuesArray = obj.optJSONArray("formCues") ?: JSONArray()
                    val cues = mutableListOf<String>()
                    for (j in 0 until cuesArray.length()) {
                        cues.add(cuesArray.getString(j))
                    }
                    list.add(
                        RoutineExercise(
                            id = obj.optString("id", "ex_$i"),
                            name = obj.optString("name", "Exercise"),
                            targetSets = obj.optInt("targetSets", 3),
                            targetRepsOrDuration = obj.optString("targetRepsOrDuration", "10 reps"),
                            shortInstructions = obj.optString("shortInstructions", ""),
                            formCues = cues,
                            muscleGroup = obj.optString("muscleGroup", "Upper Body"),
                            isDurationBased = obj.optBoolean("isDurationBased", false),
                            defaultDurationSeconds = obj.optInt("defaultDurationSeconds", 30)
                        )
                    )
                }
            } catch (_: Exception) {}
            return list
        }

        fun routineDayToDayProgram(day: RoutineDayEntity): DayProgram {
            val routineExercises = jsonToRoutineExercises(day.exercisesJson)
            val exercises = routineExercises.map { re ->
                Exercise(
                    id = re.id,
                    name = re.name,
                    targetSets = re.targetSets,
                    targetRepsOrDuration = re.targetRepsOrDuration,
                    shortInstructions = re.shortInstructions,
                    formCues = re.formCues,
                    muscleGroup = re.muscleGroup,
                    isDurationBased = re.isDurationBased,
                    defaultDurationSeconds = re.defaultDurationSeconds
                )
            }
            return DayProgram(
                dayName = day.dayName,
                workoutName = day.workoutName,
                isRestDay = day.isRestDay,
                focusDescription = day.focusDescription,
                exercises = exercises
            )
        }
    }
}

