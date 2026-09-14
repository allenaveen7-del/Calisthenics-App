package com.example.data.model

data class RoutineExercise(
    val id: String,
    val name: String,
    val targetSets: Int,
    val targetRepsOrDuration: String,
    val shortInstructions: String,
    val formCues: List<String> = emptyList(),
    val muscleGroup: String = "Upper Body",
    val isDurationBased: Boolean = false,
    val defaultDurationSeconds: Int = 30
)

data class ProposedDay(
    val dayName: String,
    val workoutName: String,
    val isRestDay: Boolean,
    val focusDescription: String,
    val exercises: List<RoutineExercise> = emptyList()
)

data class ProposedProgram(
    val title: String,
    val description: String,
    val totalWeeks: Int = 8,
    val days: List<ProposedDay>
)

data class ProgressionInsight(
    val title: String,
    val description: String,
    val type: InsightType,
    val actionText: String? = null
)

enum class InsightType {
    LEVEL_UP,
    DELOAD,
    MILESTONE,
    RECOVERY,
    TECHNIQUE
}

data class StudyMeReport(
    val adherenceScore: Int, // 0 - 100
    val totalWorkouts: Int,
    val currentStreak: Int,
    val strengthAssessment: String,
    val skillReadiness: String,
    val keyStrengths: List<String>,
    val areasToImprove: List<String>,
    val recommendedNextPhase: String
)

data class CoachChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "user" or "coach"
    val text: String,
    val proposal: ProposedProgram? = null,
    val timestamp: Long = System.currentTimeMillis()
)

