package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutRecordDao {
    @Query("SELECT * FROM workout_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<WorkoutRecord>>

    @Query("SELECT COUNT(*) FROM workout_records")
    fun getCompletedWorkoutsCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: WorkoutRecord): Long

    @Query("DELETE FROM workout_records")
    suspend fun clearAll()
}

@Dao
interface PersonalRecordDao {
    @Query("SELECT * FROM personal_records ORDER BY category ASC, recordKey ASC")
    fun getAllPRs(): Flow<List<PersonalRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePR(pr: PersonalRecord)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(prs: List<PersonalRecord>)

    @Query("DELETE FROM personal_records")
    suspend fun clearAll()
}

@Dao
interface SkillProgressDao {
    @Query("SELECT * FROM skill_progress")
    fun getAllSkillProgress(): Flow<List<SkillProgress>>

    @Query("SELECT * FROM skill_progress WHERE skillId = :id")
    suspend fun getSkillById(id: String): SkillProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSkillProgress(skill: SkillProgress)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(skills: List<SkillProgress>)

    @Query("UPDATE skill_progress SET currentLevel = :level WHERE skillId = :id")
    suspend fun updateLevel(id: String, level: Int)

    @Query("UPDATE skill_progress SET bestHoldSeconds = :holdSeconds WHERE skillId = :id")
    suspend fun updateBestHold(id: String, holdSeconds: Int)

    @Query("DELETE FROM skill_progress")
    suspend fun clearAll()
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileSync(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    @Query("UPDATE user_profile SET fitnessLevel = :level WHERE id = 1")
    suspend fun updateFitnessLevel(level: String)

    @Query("UPDATE user_profile SET primaryGoal = :primary, secondaryGoal = :secondary WHERE id = 1")
    suspend fun updateGoals(primary: String, secondary: String)

    @Query("UPDATE user_profile SET equipmentAvailable = :equipment WHERE id = 1")
    suspend fun updateEquipment(equipment: String)

    @Query("UPDATE user_profile SET currentProgramPhase = :phase, currentWeek = :currentWeek, totalWeeksInPhase = :totalWeeks WHERE id = 1")
    suspend fun updateProgramPhase(phase: String, currentWeek: Int, totalWeeks: Int)

    @Query("DELETE FROM user_profile")
    suspend fun clearAll()
}

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises ORDER BY category ASC, progressionTier ASC, name ASC")
    fun getAllExercises(): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE category = :category ORDER BY progressionTier ASC, name ASC")
    fun getExercisesByCategory(category: String): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE id = :id")
    suspend fun getExerciseById(id: String): ExerciseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: ExerciseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<ExerciseEntity>)

    @Query("DELETE FROM exercises WHERE id = :id")
    suspend fun deleteExerciseById(id: String)

    @Query("DELETE FROM exercises")
    suspend fun clearAll()
}

@Dao
interface ProgramDao {
    @Query("SELECT * FROM custom_programs ORDER BY createdAt DESC")
    fun getAllPrograms(): Flow<List<CustomProgramEntity>>

    @Query("SELECT * FROM custom_programs WHERE isActive = 1 LIMIT 1")
    fun getActiveProgram(): Flow<CustomProgramEntity?>

    @Query("SELECT * FROM custom_programs WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveProgramSync(): CustomProgramEntity?

    @Query("SELECT * FROM custom_programs WHERE id = :id")
    suspend fun getProgramById(id: String): CustomProgramEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgram(program: CustomProgramEntity)

    @Query("UPDATE custom_programs SET isActive = (CASE WHEN id = :id THEN 1 ELSE 0 END)")
    suspend fun setActiveProgram(id: String)

    @Query("DELETE FROM custom_programs WHERE id = :id")
    suspend fun deleteProgram(id: String)

    @Query("SELECT * FROM routine_days WHERE programId = :programId ORDER BY id ASC")
    fun getDaysForProgram(programId: String): Flow<List<RoutineDayEntity>>

    @Query("SELECT * FROM routine_days WHERE programId = :programId ORDER BY id ASC")
    suspend fun getDaysForProgramSync(programId: String): List<RoutineDayEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineDay(day: RoutineDayEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineDays(days: List<RoutineDayEntity>)

    @Query("DELETE FROM routine_days WHERE programId = :programId")
    suspend fun deleteDaysForProgram(programId: String)

    @Query("DELETE FROM custom_programs")
    suspend fun clearAllPrograms()

    @Query("DELETE FROM routine_days")
    suspend fun clearAllDays()
}
