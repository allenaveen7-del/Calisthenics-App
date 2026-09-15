package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_records")
data class WorkoutRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val workoutName: String,
    val dayOfWeek: String,
    val timestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Int,
    val completedExercisesCount: Int,
    val totalExercisesCount: Int,
    val dateFormatted: String,
    val caloriesBurned: Int = 240,
    val heartRateAvg: Int = 135,
    val totalVolumeKg: Int = 0
)

@Entity(tableName = "personal_records")
data class PersonalRecord(
    @PrimaryKey
    val recordKey: String, // e.g. "Pull-ups", "Dips", "Push-ups", "Handstand Hold", etc.
    val exerciseName: String,
    val recordValue: Int, // reps or seconds
    val unit: String, // "reps" or "s"
    val category: String, // "reps" or "hold"
    val dateAchieved: String
)

@Entity(tableName = "skill_progress")
data class SkillProgress(
    @PrimaryKey
    val skillId: String, // "handstand", "lsit", "planche", "front_lever", "muscle_up"
    val skillName: String,
    val currentLevel: Int = 1,
    val maxLevel: Int = 5,
    val notes: String = "",
    val bestHoldSeconds: Int = 0
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val displayName: String = "Alex Vance",
    val username: String = "alex_titan",
    val avatarId: String = "avatar_falcon",
    val age: Int = 24,
    val gender: String = "Male",
    val heightCm: Float = 178f,
    val weightKg: Float = 74.5f,
    val isMetric: Boolean = true,
    val isOnboardingCompleted: Boolean = true,
    val authProvider: String = "Google",
    val userEmail: String = "crmyhsk@gmail.com",
    val fitnessLevel: String = "Intermediate", // Beginner, Intermediate, Advanced, Elite
    val experienceYears: String = "1-2 years",
    val primaryGoal: String = "Skill Mastery & Strength",
    val secondaryGoal: String = "Muscle-up & Planche",
    val equipmentAvailable: String = "Pull-up Bar, Dip Station, Floor, Rings",
    val trainingDaysPerWeek: Int = 5,
    val workoutDurationMinutes: Int = 45,
    val jointLimitations: String = "None",
    val currentProgramPhase: String = "Phase 1: Athletic Foundation & Hypertrophy",
    val currentWeek: Int = 1,
    val totalWeeksInPhase: Int = 8,
    val notes: String = "",
    val bodyMetricsJson: String = "[]",
    val favoritedSkillIdsJson: String = "[\"planche\", \"handstand\"]"
)

@Entity(tableName = "community_posts")
data class CommunityPostEntity(
    @PrimaryKey
    val id: String,
    val authorName: String,
    val authorHandle: String,
    val authorRankTier: String,
    val avatarId: String,
    val timeAgo: String,
    val content: String,
    val workoutTag: String = "",
    val prTag: String = "",
    val likesCount: Int = 12,
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
    val commentsCount: Int = 2,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val category: String, // "Push", "Pull", "Legs", "Core", "Skill", "Mobility"
    val progressionTier: Int = 1, // 1 - 5
    val targetSets: Int = 3,
    val targetRepsOrDuration: String = "8 - 12 reps",
    val isDurationBased: Boolean = false,
    val defaultDurationSeconds: Int = 30,
    val muscleGroup: String = "Upper Body",
    val equipmentRequired: String = "Floor / Bodyweight",
    val shortInstructions: String = "",
    val formCuesJson: String = "[]",
    val isCustom: Boolean = false
)

@Entity(tableName = "custom_programs")
data class CustomProgramEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val totalWeeks: Int = 8,
    val currentWeek: Int = 1,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "routine_days")
data class RoutineDayEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val programId: String,
    val dayName: String, // "Monday", "Tuesday", etc.
    val workoutName: String,
    val isRestDay: Boolean = false,
    val focusDescription: String = "",
    val exercisesJson: String = "[]" // JSON representation of exercises list
)

