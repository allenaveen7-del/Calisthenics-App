package com.example.data.api

import com.example.BuildConfig
import com.example.data.local.PersonalRecord
import com.example.data.local.SkillProgress
import com.example.data.local.UserProfileEntity
import com.example.data.local.WorkoutRecord
import com.example.data.model.ProposedDay
import com.example.data.model.ProposedProgram
import com.example.data.model.RoutineExercise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class AiCoachService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generateCoachResponse(
        userPrompt: String,
        profile: UserProfileEntity?,
        prs: List<PersonalRecord>,
        skills: List<SkillProgress>,
        records: List<WorkoutRecord>
    ): Pair<String, ProposedProgram?> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        val hasValidApiKey = apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY"

        if (hasValidApiKey) {
            try {
                val apiResult = callGeminiApi(apiKey, userPrompt, profile, prs, skills, records)
                if (apiResult.second != null || apiResult.first.isNotBlank()) {
                    return@withContext apiResult
                }
            } catch (_: Exception) {
                // Network error or rate limit, fall back to offline coach engine
            }
        }

        // Offline Adaptive Coach Fallback
        return@withContext generateOfflineAdaptiveResponse(userPrompt, profile, prs, skills, records)
    }

    private fun callGeminiApi(
        apiKey: String,
        userPrompt: String,
        profile: UserProfileEntity?,
        prs: List<PersonalRecord>,
        skills: List<SkillProgress>,
        records: List<WorkoutRecord>
    ): Pair<String, ProposedProgram?> {
        val prSummary = prs.joinToString(", ") { "${it.exerciseName}: ${it.recordValue}${it.unit}" }
        val skillSummary = skills.joinToString(", ") { "${it.skillName}: Lvl ${it.currentLevel}/5 (Best: ${it.bestHoldSeconds}s)" }
        val equipment = profile?.equipmentAvailable ?: "Floor, Pull-up Bar, Dip Station"
        val fitnessLevel = profile?.fitnessLevel ?: "Intermediate"
        val goals = "${profile?.primaryGoal ?: "Strength"} / ${profile?.secondaryGoal ?: "Skills"}"

        val systemPrompt = """
            You are a master calisthenics coach. Analyze the user's profile and request.
            User Profile:
            - Fitness Level: $fitnessLevel
            - Available Equipment: $equipment
            - Goals: $goals
            - Personal Records: $prSummary
            - Skill Levels: $skillSummary
            - Total Workouts Logged: ${records.size}

            You MUST reply with valid JSON ONLY in this format:
            {
              "coachMessage": "Explanation of the program rationale and technical tips.",
              "program": {
                "title": "Program Phase Title",
                "description": "Short overview of weekly structure",
                "totalWeeks": 8,
                "days": [
                  {
                    "dayName": "Monday",
                    "workoutName": "Upper Push & Planche",
                    "isRestDay": false,
                    "focusDescription": "Chest, triceps, anterior delts",
                    "exercises": [
                      {
                        "id": "gem_1",
                        "name": "Pike Push-ups",
                        "targetSets": 4,
                        "targetRepsOrDuration": "8 - 10 reps",
                        "shortInstructions": "Tripod head path, push floor away",
                        "formCues": ["Elbows 45 degrees", "Straight legs"],
                        "muscleGroup": "Shoulders",
                        "isDurationBased": false,
                        "defaultDurationSeconds": 0
                      }
                    ]
                  }
                ]
              }
            }
        """.trimIndent()

        val jsonRequest = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", "$systemPrompt\n\nUser Request: $userPrompt"))
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.7)
            })
        }

        val requestBody = jsonRequest.toString().toRequestBody("application/json".toMediaType())
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: return Pair("Unable to connect to coach service.", null)

        val rootObj = JSONObject(responseBody)
        val candidates = rootObj.optJSONArray("candidates") ?: return Pair("No response from coach.", null)
        val firstCandidate = candidates.optJSONObject(0) ?: return Pair("No candidate returned.", null)
        val content = firstCandidate.optJSONObject("content")
        val parts = content?.optJSONArray("parts")
        val text = parts?.optJSONObject(0)?.optString("text") ?: ""

        val parsedJson = JSONObject(text)
        val message = parsedJson.optString("coachMessage", "Here is your customized calisthenics plan:")
        val progObj = parsedJson.optJSONObject("program")

        val program = if (progObj != null) parseProgramFromJson(progObj) else null
        return Pair(message, program)
    }

    private fun parseProgramFromJson(progObj: JSONObject): ProposedProgram {
        val title = progObj.optString("title", "Custom Adaptive Routine")
        val description = progObj.optString("description", "Targeted progression plan")
        val weeks = progObj.optInt("totalWeeks", 8)
        val daysArray = progObj.optJSONArray("days") ?: JSONArray()

        val days = mutableListOf<ProposedDay>()
        for (i in 0 until daysArray.length()) {
            val dayObj = daysArray.getJSONObject(i)
            val dayName = dayObj.optString("dayName", "Day ${i + 1}")
            val workoutName = dayObj.optString("workoutName", "Training Session")
            val isRest = dayObj.optBoolean("isRestDay", false)
            val focus = dayObj.optString("focusDescription", "")
            val exercisesArray = dayObj.optJSONArray("exercises") ?: JSONArray()

            val exercises = mutableListOf<RoutineExercise>()
            for (j in 0 until exercisesArray.length()) {
                val exObj = exercisesArray.getJSONObject(j)
                val cuesArray = exObj.optJSONArray("formCues") ?: JSONArray()
                val cuesList = mutableListOf<String>()
                for (k in 0 until cuesArray.length()) {
                    cuesList.add(cuesArray.getString(k))
                }

                exercises.add(
                    RoutineExercise(
                        id = exObj.optString("id", "ex_${i}_${j}"),
                        name = exObj.optString("name", "Exercise"),
                        targetSets = exObj.optInt("targetSets", 3),
                        targetRepsOrDuration = exObj.optString("targetRepsOrDuration", "10 reps"),
                        shortInstructions = exObj.optString("shortInstructions", ""),
                        formCues = cuesList,
                        muscleGroup = exObj.optString("muscleGroup", "Full Body"),
                        isDurationBased = exObj.optBoolean("isDurationBased", false),
                        defaultDurationSeconds = exObj.optInt("defaultDurationSeconds", 30)
                    )
                )
            }

            days.add(
                ProposedDay(
                    dayName = dayName,
                    workoutName = workoutName,
                    isRestDay = isRest,
                    focusDescription = focus,
                    exercises = exercises
                )
            )
        }

        return ProposedProgram(
            title = title,
            description = description,
            totalWeeks = weeks,
            days = days
        )
    }

    private fun generateOfflineAdaptiveResponse(
        userPrompt: String,
        profile: UserProfileEntity?,
        prs: List<PersonalRecord>,
        skills: List<SkillProgress>,
        records: List<WorkoutRecord>
    ): Pair<String, ProposedProgram> {
        val lower = userPrompt.lowercase()
        val prMap = prs.associateBy { it.recordKey }
        val pullUps = prMap["pull_ups"]?.recordValue ?: 8
        val dips = prMap["dips"]?.recordValue ?: 12
        val pushUps = prMap["push_ups"]?.recordValue ?: 20

        val daysPerWeek = profile?.trainingDaysPerWeek ?: 5
        val level = profile?.fitnessLevel ?: "Intermediate"

        val isRingFocus = lower.contains("ring")
        val isFrontLeverFocus = lower.contains("front lever") || lower.contains("lever")
        val isMinimalist = lower.contains("minimal") || lower.contains("short") || lower.contains("quick")
        val isStudyMe = lower.contains("study") || lower.contains("analy") || lower.contains("upgrade")

        val title: String
        val rationale: String
        val programDays: List<ProposedDay>

        if (isRingFocus) {
            title = "8-Week Gymnastic Rings Mastery Phase"
            rationale = "Adapted for ring training: Unstable ring mechanics dramatically stimulate stabilizing musculature (rotator cuff, serratus, pecs) while sparing joint connective tissue through natural rotation."
            programDays = createRingSchedule()
        } else if (isFrontLeverFocus) {
            title = "10-Week Front Lever & Horizontal Pull Specialization"
            rationale = "Structured around lat recruitment, scapular depression, and core compression. Integrates inverted rows, tuck holds, and straight-arm levers with progressive volume."
            programDays = createFrontLeverSchedule()
        } else if (isMinimalist) {
            title = "4-Day High-Frequency Compound Routine"
            rationale = "Minimalist 35-minute sessions centered on highest-yield compound multi-joint movements: Pull-ups, Dips, Elevated Pikes, Bulgarian Split Squats, and L-sits."
            programDays = createMinimalistSchedule()
        } else if (isStudyMe || lower.contains("upgrade")) {
            title = "Phase 2: Advanced Relative Strength & Skill Progression"
            rationale = "Based on your training memory (${records.size} logged sessions, ${pullUps} max pull-ups, ${dips} dips), this upgraded 8-week phase raises the progression tier: introducing Archer Pull-ups, Feet-Elevated Pike Push-ups, and Level 3 Skill holds."
            programDays = createUpgradedSchedule(pullUps, dips, pushUps)
        } else {
            title = "Customized ${level} Adaptive Program"
            rationale = "Tailored specifically for ${daysPerWeek} training days/week using ${profile?.equipmentAvailable ?: "Bars & Floor"}. Balanced push/pull volume with built-in active skill practice and restorative rest days."
            programDays = createUpgradedSchedule(pullUps, dips, pushUps)
        }

        val message = "$rationale\n\nReview the proposed schedule below. Tap 'Accept & Apply to Schedule' to set this as your active training program in the database."

        val program = ProposedProgram(
            title = title,
            description = "Tailored ${daysPerWeek}-day adaptive progression designed for $level athletes.",
            totalWeeks = 8,
            days = programDays
        )

        return Pair(message, program)
    }

    private fun createRingSchedule(): List<ProposedDay> {
        return listOf(
            ProposedDay(
                dayName = "Monday",
                workoutName = "Ring Push & Support",
                isRestDay = false,
                focusDescription = "Chest, triceps & ring support hold stability",
                exercises = listOf(
                    RoutineExercise("r1", "Ring Support Hold (Turned Out)", 3, "20 - 30s hold", "Hold locked top position with rings turned out 45 degrees.", listOf("Lock elbows", "Press shoulders down"), "Shoulders & Core", true, 25),
                    RoutineExercise("r2", "Ring Dips", 4, "6 - 8 reps", "Lower deep with rings close to ribs, press to full turnout lockout.", listOf("Control the shake", "Deep range"), "Chest & Triceps"),
                    RoutineExercise("r3", "Ring Push-ups", 3, "10 - 12 reps", "Squeeze rings together at the peak for intense pec contraction.", listOf("Keep body tight", "Full lockout"), "Chest"),
                    RoutineExercise("r4", "Ring Tricep Extensions", 3, "8 - 10 reps", "Bodyweight skull crushers using ring straps.", listOf("Elbows stationary", "Feel triceps stretch"), "Triceps")
                )
            ),
            ProposedDay(
                dayName = "Tuesday",
                workoutName = "Ring Pull & False Grip",
                isRestDay = false,
                focusDescription = "Lats, biceps & false grip conditioning",
                exercises = listOf(
                    RoutineExercise("r5", "False Grip Ring Pull-ups", 4, "5 - 8 reps", "Wrist crease placed over rings for direct muscle-up transition strength.", listOf("Pull deep to sternum", "False grip locked"), "Lats & Forearms"),
                    RoutineExercise("r6", "Inverted Ring Rows", 3, "10 - 12 reps", "Horizontal body row with free wrist rotation.", listOf("Squeeze scapulae", "Pause 1s at top"), "Upper Back"),
                    RoutineExercise("r7", "Ring Bicep Curls", 3, "10 reps", "Lean back and curl rings to forehead.", listOf("Elbows high", "Strict control"), "Biceps"),
                    RoutineExercise("r8", "Tuck Front Lever on Rings", 3, "15 - 20s hold", "Straight arm hold pulling rings toward hips.", listOf("Locked elbows", "Dome upper back"), "Lats & Core", true, 20)
                )
            ),
            ProposedDay("Wednesday", "Rest & Mobility", true, "Tissue recovery and active stretching"),
            ProposedDay(
                dayName = "Thursday",
                workoutName = "Legs & Ring Core",
                isRestDay = false,
                focusDescription = "Pistol squat progressions and ring rollouts",
                exercises = listOf(
                    RoutineExercise("r9", "Pistol Squats (Assisted on Ring)", 4, "6 - 8 reps/leg", "Single leg squat holding ring for light balance assist.", listOf("Chest up", "Drive through heel"), "Quads & Glutes"),
                    RoutineExercise("r10", "Bulgarian Split Squats", 3, "10 reps/leg", "Rear foot elevated on box or chair.", listOf("Deep stretch", "Knee tracking"), "Quads"),
                    RoutineExercise("r11", "Ring Ab Rollouts", 3, "8 - 10 reps", "From kneeling, roll rings forward into extended hollow body.", listOf("Do not arch lower back", "Pull with lats"), "Deep Core"),
                    RoutineExercise("r12", "Single-Leg Calf Raises", 3, "15 reps/leg", "Full ankle range of motion with pause at top.", listOf("Control tempo", "Squeeze top"), "Calves")
                )
            ),
            ProposedDay(
                dayName = "Friday",
                workoutName = "Upper Body Hypertrophy",
                isRestDay = false,
                focusDescription = "High-volume supersets and pump work",
                exercises = listOf(
                    RoutineExercise("r13", "Wide Ring Push-ups", 3, "12 reps", "Hands wide for outer chest recruitment.", listOf("Full depth", "Squeeze in"), "Chest"),
                    RoutineExercise("r14", "Archer Ring Rows", 3, "6 reps/side", "Rowing to one arm while opposite extends straight.", listOf("Locked straight arm", "Controlled pull"), "Upper Back"),
                    RoutineExercise("r15", "Ring Facepulls", 3, "12 reps", "Pull rings to ears, rotate elbows back.", listOf("Rear delt squeeze", "Protect shoulders"), "Rear Delts"),
                    RoutineExercise("r16", "L-sit on Rings", 3, "15 - 25s hold", "Support on rings with legs extended horizontally.", listOf("Keep rings still", "Push down"), "Core & Hip Flexors", true, 20)
                )
            ),
            ProposedDay("Saturday", "Skill Practice & Active Recovery", false, "Light balance and handstand drills", listOf(
                RoutineExercise("r17", "Chest-to-Wall Handstand", 3, "30s hold", "Maintain hollow line alignment.", listOf("Active push", "Tight glutes"), "Shoulders", true, 30),
                RoutineExercise("r18", "Wrist & Shoulder Mobility Flow", 2, "60s active", "Decompress wrist joints and overhead lats.", listOf("Slow breathing", "Full circles"), "Mobility", true, 60)
            )),
            ProposedDay("Sunday", "Rest & Reset", true, "Systemic rest and recovery")
        )
    }

    private fun createFrontLeverSchedule(): List<ProposedDay> {
        return listOf(
            ProposedDay(
                dayName = "Monday",
                workoutName = "Front Lever & Heavy Pull",
                isRestDay = false,
                focusDescription = "Straight-arm lat leverage and vertical pulling power",
                exercises = listOf(
                    RoutineExercise("fl1", "Tuck / Adv Tuck Front Lever", 4, "15 - 20s hold", "Straight arms, hips level with shoulders, depress scapulae.", listOf("Look at toes", "Straight arms locked"), "Lats & Core", true, 20),
                    RoutineExercise("fl2", "Weighted / Strict Pull-ups", 4, "6 - 8 reps", "Full range pull-ups with sternum touching bar.", listOf("No kipping", "2s negative"), "Lats & Biceps"),
                    RoutineExercise("fl3", "Horizontal Bar Rows", 3, "10 reps", "Pull chest firmly against bar.", listOf("Pause at chest", "Squeeze lats"), "Upper Back"),
                    RoutineExercise("fl4", "Front Lever Scapular Pulls", 3, "8 reps", "Hang beneath bar, elevate into horizontal lever using only scapulae.", listOf("Arms stay straight", "Lat focus"), "Scapular Control")
                )
            ),
            ProposedDay(
                dayName = "Tuesday",
                workoutName = "Push & Handstand",
                isRestDay = false,
                focusDescription = "Overhead balance and pushing counter-balance",
                exercises = listOf(
                    RoutineExercise("fl5", "Chest-to-Wall Handstand Hold", 4, "35s hold", "Overhead straight-line posture.", listOf("Push ground away", "Point toes"), "Shoulders", true, 35),
                    RoutineExercise("fl6", "Pike Push-ups / Feet Elevated", 3, "8 - 10 reps", "Vertical pushing power to balance pulling volume.", listOf("Elbows tucked", "Tripod head path"), "Shoulders"),
                    RoutineExercise("fl7", "Parallel Dips", 3, "10 - 12 reps", "Full depth pressing for chest and triceps.", listOf("Lockout top", "Stay upright"), "Chest & Triceps")
                )
            ),
            ProposedDay("Wednesday", "Rest & Core Mobility", true, "Rest day for central nervous system recovery"),
            ProposedDay(
                dayName = "Thursday",
                workoutName = "Lever Negatives & Upper Back",
                isRestDay = false,
                focusDescription = "Eccentric overload and core compression",
                exercises = listOf(
                    RoutineExercise("fl8", "Full Front Lever Eccentrics", 4, "3 slow reps", "Invert on bar, lower down with straight legs over 4-5 seconds.", listOf("Resist gravity", "Keep hips up"), "Lats & Core"),
                    RoutineExercise("fl9", "Dragon Flag Negatives", 3, "5 reps", "Lower straight body from vertical candle on bench.", listOf("Straight line", "Tight abs"), "Core"),
                    RoutineExercise("fl10", "Chin-ups", 3, "8 - 10 reps", "Underhand grip for bicep tendon strength.", listOf("Dead hang to chin clear", "Controlled"), "Biceps & Lats")
                )
            ),
            ProposedDay(
                dayName = "Friday",
                workoutName = "Legs & Planche Counterpart",
                isRestDay = false,
                focusDescription = "Unilateral leg strength and straight-arm push",
                exercises = listOf(
                    RoutineExercise("fl11", "Bulgarian Split Squats", 4, "10 reps/leg", "Deep knee flexion and hip extension.", listOf("Tall posture", "Drive front heel"), "Quads & Glutes"),
                    RoutineExercise("fl12", "Planche Lean Hold", 4, "25s hold", "Balances straight-arm pulling with straight-arm pushing.", listOf("Protract upper back", "Lock elbows"), "Shoulders", true, 25),
                    RoutineExercise("fl13", "Hanging Straight Leg Raises", 3, "10 reps", "Toes to bar with zero swing.", listOf("Strict negative", "Exhale up"), "Core")
                )
            ),
            ProposedDay(
                dayName = "Saturday",
                workoutName = "Endurance & Grip Volume",
                isRestDay = false,
                focusDescription = "High rep calisthenics capacity",
                exercises = listOf(
                    RoutineExercise("fl14", "Standard Pull-ups", 3, "10 reps", "Clean rhythmic tempo.", listOf("Smooth cadence", "Full lockout"), "Lats"),
                    RoutineExercise("fl15", "Push-ups", 3, "20 reps", "Solid chest pump.", listOf("Core tight", "Full extension"), "Chest"),
                    RoutineExercise("fl16", "Active Bar Hang", 3, "45s hold", "Decompresses spine and builds forearm grip resilience.", listOf("Active lats", "Calm breath"), "Forearms", true, 45)
                )
            ),
            ProposedDay("Sunday", "Rest & Reset", true, "Complete rest")
        )
    }

    private fun createMinimalistSchedule(): List<ProposedDay> {
        return listOf(
            ProposedDay(
                dayName = "Monday",
                workoutName = "Minimalist Push 1",
                isRestDay = false,
                focusDescription = "35-minute maximum density chest and shoulders",
                exercises = listOf(
                    RoutineExercise("m1", "Parallel Bar Dips", 4, "8 - 12 reps", "Deep lean forward for chest activation.", listOf("Controlled depth", "Full lockout"), "Chest & Triceps"),
                    RoutineExercise("m2", "Pike Push-ups", 3, "8 - 10 reps", "Shoulder vertical press.", listOf("Tripod path", "Straight legs"), "Shoulders"),
                    RoutineExercise("m3", "Diamond Push-ups", 3, "12 reps", "Tricep finisher.", listOf("Elbows tucked", "Fast tempo"), "Triceps")
                )
            ),
            ProposedDay(
                dayName = "Tuesday",
                workoutName = "Minimalist Pull 1",
                isRestDay = false,
                focusDescription = "35-minute lats and bicep compound density",
                exercises = listOf(
                    RoutineExercise("m4", "Strict Pull-ups", 4, "6 - 10 reps", "Sternum to bar, zero kip.", listOf("Dead hang start", "Elbows down"), "Lats"),
                    RoutineExercise("m5", "Inverted Rows", 3, "10 - 12 reps", "Horizontal upper back contraction.", listOf("Squeeze shoulder blades", "Straight body"), "Upper Back"),
                    RoutineExercise("m6", "Hanging Leg Raises", 3, "10 reps", "Direct pelvic curl core.", listOf("No swinging", "Exhale up"), "Core")
                )
            ),
            ProposedDay("Wednesday", "Rest Day", true, "Midweek recovery"),
            ProposedDay(
                dayName = "Thursday",
                workoutName = "Minimalist Legs & Core",
                isRestDay = false,
                focusDescription = "Lower body unilateral power and anti-extension core",
                exercises = listOf(
                    RoutineExercise("m7", "Bulgarian Split Squats", 4, "10 reps/leg", "Deep single leg squats.", listOf("Torso upright", "Controlled"), "Quads & Glutes"),
                    RoutineExercise("m8", "Hollow Body Hold", 3, "40s hold", "Deep abdominal compression.", listOf("Flat back on floor", "Point toes"), "Core", true, 40),
                    RoutineExercise("m9", "Single-Leg Calf Raises", 3, "15 reps/leg", "Ankle stability.", listOf("Pause at top", "Full stretch"), "Calves")
                )
            ),
            ProposedDay(
                dayName = "Friday",
                workoutName = "Minimalist Upper Full",
                isRestDay = false,
                focusDescription = "Push-pull superset integration",
                exercises = listOf(
                    RoutineExercise("m10", "Chin-ups", 4, "8 - 10 reps", "Bicep and lat power.", listOf("Full extension", "Clear chin"), "Biceps & Lats"),
                    RoutineExercise("m11", "Push-ups", 4, "15 - 20 reps", "Horizontal push endurance.", listOf("Chest to floor", "Locked elbows"), "Chest"),
                    RoutineExercise("m12", "L-sit Hold", 3, "20s hold", "Compression core.", listOf("Arms locked", "Push shoulders down"), "Core", true, 20)
                )
            ),
            ProposedDay("Saturday", "Rest Day", true, "Weekend rest"),
            ProposedDay("Sunday", "Rest Day", true, "Weekly reset")
        )
    }

    private fun createUpgradedSchedule(pullUps: Int, dips: Int, pushUps: Int): List<ProposedDay> {
        val pullExercise = if (pullUps >= 10) {
            RoutineExercise("u_pull", "Archer Pull-ups / High Pull-ups", 4, "6 - 8 reps", "High explosive drive or wide archer transition.", listOf("Explosive intent", "Control eccentric"), "Lats")
        } else {
            RoutineExercise("u_pull", "Standard Pull-ups", 4, "8 - 10 reps", "Strict dead hang to bar.", listOf("Chin clear", "Smooth tempo"), "Lats")
        }

        val pushExercise = if (dips >= 14) {
            RoutineExercise("u_push", "Deep Dips with Forward Lean", 4, "10 - 12 reps", "Weighted or deep bodyweight dips.", listOf("Lock out clean", "Elbows back"), "Chest & Triceps")
        } else {
            RoutineExercise("u_push", "Parallel Bar Dips", 4, "8 - 10 reps", "Strict form and controlled depth.", listOf("Pack shoulders down", "Full lockout"), "Chest & Triceps")
        }

        return listOf(
            ProposedDay(
                dayName = "Monday",
                workoutName = "Push + Handstand Overload",
                isRestDay = false,
                focusDescription = "Overhead pressing power and static balance",
                exercises = listOf(
                    RoutineExercise("u1", "Feet-Elevated Pike Push-ups", 4, "8 - 10 reps", "Elevate feet on bench to increase vertical load.", listOf("Tripod path", "Push ground away"), "Shoulders"),
                    pushExercise,
                    RoutineExercise("u2", "Chest-to-Wall Handstand Hold", 3, "40s hold", "Hollow body inverted balance line.", listOf("Grip fingertips", "Point toes"), "Shoulders & Balance", true, 40),
                    RoutineExercise("u3", "Diamond Push-ups", 3, "12 - 15 reps", "Close grip tricep and sternum stimulus.", listOf("Elbows tucked", "Core locked"), "Triceps")
                )
            ),
            ProposedDay(
                dayName = "Tuesday",
                workoutName = "Pull + L-sit Compression",
                isRestDay = false,
                focusDescription = "Vertical pulling density and hip flexor core",
                exercises = listOf(
                    pullExercise,
                    RoutineExercise("u4", "Inverted Rows (Feet Elevated)", 3, "10 - 12 reps", "Horizontal pulling to build rhomboids and rear delts.", listOf("Squeeze shoulder blades", "Pause at top"), "Upper Back"),
                    RoutineExercise("u5", "Full Tuck / 1-Leg L-sit Hold", 4, "20 - 25s hold", "Elevate hips on parallettes and extend legs.", listOf("Push shoulders down", "Toes pointed"), "Core & Hip Flexors", true, 20),
                    RoutineExercise("u6", "Active Bar Hang & Scapular Pulls", 3, "30s + 8 pulls", "Grip conditioning and scapular depression.", listOf("Straight arms", "Retract down"), "Grip & Scapulae", true, 30)
                )
            ),
            ProposedDay("Wednesday", "Rest & Active Mobility", true, "Tissue repair and deep joint mobility"),
            ProposedDay(
                dayName = "Thursday",
                workoutName = "Legs + Anti-Extension Core",
                isRestDay = false,
                focusDescription = "Unilateral quad strength and pelvic control",
                exercises = listOf(
                    RoutineExercise("u7", "Pistol Squat Progressions", 4, "6 - 8 reps/leg", "Single leg knee flexion with upright torso.", listOf("Drive mid-foot", "Arms forward"), "Quads & Glutes"),
                    RoutineExercise("u8", "Walking Lunges", 3, "12 reps/leg", "Continuous stride with deep knee bend.", listOf("Torso tall", "Stable knee tracking"), "Quads"),
                    RoutineExercise("u9", "Hollow Body Hold", 3, "40s hold", "Flatten lower spine completely into floor.", listOf("Banana shape", "Locked knees"), "Core", true, 40),
                    RoutineExercise("u10", "Hanging Straight Leg Raises", 3, "10 - 12 reps", "Strict curl of pelvis with straight legs.", listOf("Exhale on lift", "Zero momentum"), "Lower Abs")
                )
            ),
            ProposedDay(
                dayName = "Friday",
                workoutName = "Push + Planche Conditioning",
                isRestDay = false,
                focusDescription = "Straight-arm shoulder lean and dip power",
                exercises = listOf(
                    RoutineExercise("u11", "Planche Lean Hold", 4, "25 - 30s hold", "Fingers turned out, dome upper back and lean past wrists.", listOf("Straight arms like steel", "Tuck pelvis"), "Anterior Delts", true, 25),
                    RoutineExercise("u12", "Pseudo Planche Push-ups", 3, "8 - 10 reps", "Push-ups maintaining extreme forward lean.", listOf("Stay forward at top", "High core tension"), "Shoulders"),
                    RoutineExercise("u13", "Parallel Dips", 3, "12 reps", "Clean lockout at top.", listOf("Pack shoulders", "Controlled negative"), "Triceps & Chest"),
                    RoutineExercise("u14", "Crow Pose / Frog Stand", 3, "30s balance", "Palms on floor, balance knees on triceps.", listOf("Fingertip balance", "Gaze forward"), "Balance", true, 30)
                )
            ),
            ProposedDay(
                dayName = "Saturday",
                workoutName = "Pull + Athletic Integration",
                isRestDay = false,
                focusDescription = "Explosive pull volume and posterior chain",
                exercises = listOf(
                    RoutineExercise("u15", "Chin-ups", 4, "8 - 10 reps", "Underhand bicep and lat pull.", listOf("Full hang", "Clear chin"), "Biceps & Lats"),
                    RoutineExercise("u16", "Bulgarian Split Squats", 3, "10 reps/leg", "Rear leg elevated on bench.", listOf("Deep stretch", "Heel drive"), "Quads & Glutes"),
                    RoutineExercise("u17", "L-sit Kickouts on Bars", 3, "8 - 10 reps", "Alternate tuck and extension in support.", listOf("Straight arms", "Parallel legs"), "Core"),
                    RoutineExercise("u18", "Prone Superman Hold", 3, "35s hold", "Posterior chain arch hold.", listOf("Squeeze glutes", "Reach long"), "Posterior Chain", true, 35)
                )
            ),
            ProposedDay("Sunday", "Rest & Reset", true, "Complete systemic rest")
        )
    }
}
