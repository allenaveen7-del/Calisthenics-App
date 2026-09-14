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
    val dateFormatted: String
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
    val notes: String = ""
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

