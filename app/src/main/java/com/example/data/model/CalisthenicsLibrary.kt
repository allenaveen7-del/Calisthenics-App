package com.example.data.model

import com.example.data.local.ExerciseEntity
import org.json.JSONArray

object CalisthenicsLibrary {

    private fun listToJson(cues: List<String>): String {
        val array = JSONArray()
        cues.forEach { array.put(it) }
        return array.toString()
    }

    val DefaultExercises: List<ExerciseEntity> = listOf(
        // PUSH
        ExerciseEntity(
            id = "ex_standard_pushup",
            name = "Standard Push-up",
            category = "Push",
            progressionTier = 1,
            targetSets = 3,
            targetRepsOrDuration = "12 - 15 reps",
            isDurationBased = false,
            muscleGroup = "Chest & Triceps",
            equipmentRequired = "Floor",
            shortInstructions = "Hands slightly wider than shoulders, body in a rigid plank, lower chest to 1 inch above floor.",
            formCuesJson = listToJson(listOf("Elbows at 45 degrees", "Squeeze glutes tight", "Full lockout at top"))
        ),
        ExerciseEntity(
            id = "ex_diamond_pushup",
            name = "Diamond Push-up",
            category = "Push",
            progressionTier = 2,
            targetSets = 3,
            targetRepsOrDuration = "10 - 12 reps",
            isDurationBased = false,
            muscleGroup = "Triceps & Chest",
            equipmentRequired = "Floor",
            shortInstructions = "Index fingers and thumbs touching to form a diamond shape under sternum.",
            formCuesJson = listToJson(listOf("Keep elbows tracking close to ribs", "Neutral neck", "Controlled eccentric"))
        ),
        ExerciseEntity(
            id = "ex_pike_pushup",
            name = "Pike Push-up",
            category = "Push",
            progressionTier = 3,
            targetSets = 3,
            targetRepsOrDuration = "8 - 10 reps",
            isDurationBased = false,
            muscleGroup = "Shoulders & Triceps",
            equipmentRequired = "Floor",
            shortInstructions = "Hips high in an inverted V. Lower head forward between hands in a tripod shape.",
            formCuesJson = listToJson(listOf("Push ground away at top", "Straight legs if mobile", "Elbows tucked"))
        ),
        ExerciseEntity(
            id = "ex_elevated_pike",
            name = "Feet-Elevated Pike Push-up",
            category = "Push",
            progressionTier = 4,
            targetSets = 3,
            targetRepsOrDuration = "6 - 8 reps",
            isDurationBased = false,
            muscleGroup = "Shoulders",
            equipmentRequired = "Bench / Chair",
            shortInstructions = "Feet elevated on a bench or chair to shift higher bodyweight load onto overhead pressing shoulders.",
            formCuesJson = listToJson(listOf("Vertical torso line", "Grip floor with fingertips", "Full scapular elevation"))
        ),
        ExerciseEntity(
            id = "ex_wall_hspu",
            name = "Wall Handstand Push-up",
            category = "Push",
            progressionTier = 5,
            targetSets = 3,
            targetRepsOrDuration = "4 - 6 reps",
            isDurationBased = false,
            muscleGroup = "Shoulders & Triceps",
            equipmentRequired = "Wall",
            shortInstructions = "Kick up against wall, lower crown of head forward to touch floor lightly, press up forcefully.",
            formCuesJson = listToJson(listOf("Tripod head positioning", "Lock elbows cleanly", "Core tight"))
        ),
        ExerciseEntity(
            id = "ex_parallel_dips",
            name = "Parallel Bar Dips",
            category = "Push",
            progressionTier = 3,
            targetSets = 4,
            targetRepsOrDuration = "8 - 12 reps",
            isDurationBased = false,
            muscleGroup = "Chest & Triceps",
            equipmentRequired = "Dip Station / Parallel Bars",
            shortInstructions = "Support on bars, lean slightly forward, lower until shoulders are below elbows, press to lockout.",
            formCuesJson = listToJson(listOf("Pack shoulders down", "Elbows track backward", "Clean lockout"))
        ),
        ExerciseEntity(
            id = "ex_ring_dips",
            name = "Gymnastic Ring Dips",
            category = "Push",
            progressionTier = 4,
            targetSets = 3,
            targetRepsOrDuration = "6 - 10 reps",
            isDurationBased = false,
            muscleGroup = "Chest, Triceps & Stabilizers",
            equipmentRequired = "Gymnastic Rings",
            shortInstructions = "Perform dips on free-hanging rings, turning rings out 45 degrees at top lockout (RTO).",
            formCuesJson = listToJson(listOf("Turn rings out at top", "Control shake with core", "Deep range of motion"))
        ),

        // PULL
        ExerciseEntity(
            id = "ex_australian_rows",
            name = "Inverted Bodyweight Rows",
            category = "Pull",
            progressionTier = 1,
            targetSets = 3,
            targetRepsOrDuration = "10 - 15 reps",
            isDurationBased = false,
            muscleGroup = "Upper Back & Lats",
            equipmentRequired = "Low Bar or Rings",
            shortInstructions = "Hang horizontally under bar or rings, pull chest to bar while maintaining a straight body plank.",
            formCuesJson = listToJson(listOf("Retract scapulae first", "Squeeze shoulder blades", "Don't sag hips"))
        ),
        ExerciseEntity(
            id = "ex_standard_pullups",
            name = "Standard Pull-ups",
            category = "Pull",
            progressionTier = 2,
            targetSets = 4,
            targetRepsOrDuration = "6 - 10 reps",
            isDurationBased = false,
            muscleGroup = "Lats & Biceps",
            equipmentRequired = "Pull-up Bar",
            shortInstructions = "Dead hang start, overhand grip, pull elbows down toward ribs until chin clears bar.",
            formCuesJson = listToJson(listOf("Zero kicking/kipping", "Chest toward bar", "2-second controlled descent"))
        ),
        ExerciseEntity(
            id = "ex_chin_ups",
            name = "Chin-ups",
            category = "Pull",
            progressionTier = 2,
            targetSets = 3,
            targetRepsOrDuration = "8 - 12 reps",
            isDurationBased = false,
            muscleGroup = "Biceps & Lower Lats",
            equipmentRequired = "Pull-up Bar",
            shortInstructions = "Underhand supinated grip. Excellent for bicep tendon conditioning and peak pulling strength.",
            formCuesJson = listToJson(listOf("Full elbow extension at bottom", "Drive elbows back", "Clear chin"))
        ),
        ExerciseEntity(
            id = "ex_archer_pullups",
            name = "Archer Pull-ups",
            category = "Pull",
            progressionTier = 4,
            targetSets = 3,
            targetRepsOrDuration = "4 - 6 reps per side",
            isDurationBased = false,
            muscleGroup = "Lats & Unilateral Pull",
            equipmentRequired = "Pull-up Bar or Rings",
            shortInstructions = "Wide grip. Pull up to one hand while extending the other arm completely straight along the bar.",
            formCuesJson = listToJson(listOf("Keep assisting arm locked", "Smooth lateral pull", "Switch sides"))
        ),
        ExerciseEntity(
            id = "ex_high_explosive_pullup",
            name = "Explosive High Pull-ups",
            category = "Pull",
            progressionTier = 4,
            targetSets = 3,
            targetRepsOrDuration = "4 - 6 explosive reps",
            isDurationBased = false,
            muscleGroup = "Lats & Explosive Power",
            equipmentRequired = "Pull-up Bar",
            shortInstructions = "Pull with maximum violent velocity aiming to touch sternum or belly button to bar.",
            formCuesJson = listToJson(listOf("Fast acceleration from dead hang", "Drive elbows downward", "Keep legs together"))
        ),

        // LEGS
        ExerciseEntity(
            id = "ex_air_squats",
            name = "Full Depth Air Squats",
            category = "Legs",
            progressionTier = 1,
            targetSets = 4,
            targetRepsOrDuration = "20 reps",
            isDurationBased = false,
            muscleGroup = "Quads & Glutes",
            equipmentRequired = "Floor",
            shortInstructions = "Feet shoulder-width apart. Squat deep below parallel with upright chest.",
            formCuesJson = listToJson(listOf("Knees track over toes", "Weight in mid-foot/heel", "Full hip extension"))
        ),
        ExerciseEntity(
            id = "ex_bulgarian_squat",
            name = "Bulgarian Split Squats",
            category = "Legs",
            progressionTier = 2,
            targetSets = 3,
            targetRepsOrDuration = "10 - 12 reps per leg",
            isDurationBased = false,
            muscleGroup = "Quads & Glutes",
            equipmentRequired = "Bench / Chair",
            shortInstructions = "Rear foot elevated. Lower front hip until thigh is parallel to ground, drive up through front heel.",
            formCuesJson = listToJson(listOf("Keep torso tall", "Back knee taps lightly", "Controlled eccentric"))
        ),
        ExerciseEntity(
            id = "ex_pistol_squat",
            name = "Pistol Squat (Single Leg)",
            category = "Legs",
            progressionTier = 4,
            targetSets = 3,
            targetRepsOrDuration = "5 - 8 reps per leg",
            isDurationBased = false,
            muscleGroup = "Quads, Glutes & Balance",
            equipmentRequired = "Floor",
            shortInstructions = "Balance on one leg, extend non-working leg forward horizontally. Squat all the way down and stand up.",
            formCuesJson = listToJson(listOf("Keep non-working leg off floor", "Arms reached forward for counter-balance", "Drive out of hole"))
        ),
        ExerciseEntity(
            id = "ex_nordic_curls",
            name = "Nordic Hamstring Curls (Assisted)",
            category = "Legs",
            progressionTier = 4,
            targetSets = 3,
            targetRepsOrDuration = "5 - 8 slow reps",
            isDurationBased = false,
            muscleGroup = "Hamstrings",
            equipmentRequired = "Anchor for Feet",
            shortInstructions = "Kneel with ankles anchored. Lower torso forward under strict hamstring eccentric tension.",
            formCuesJson = listToJson(listOf("Keep hips extended straight", "Resist falling with hamstrings", "Catch softly with hands"))
        ),

        // CORE
        ExerciseEntity(
            id = "ex_hollow_body",
            name = "Hollow Body Hold",
            category = "Core",
            progressionTier = 1,
            targetSets = 3,
            targetRepsOrDuration = "30 - 45s hold",
            isDurationBased = true,
            defaultDurationSeconds = 35,
            muscleGroup = "Deep Core & Anterior Chain",
            equipmentRequired = "Floor",
            shortInstructions = "Lie on back, press lumbar spine flat into floor, raise arms and straight legs so body forms a shallow banana.",
            formCuesJson = listToJson(listOf("Zero space under lower back", "Point toes", "Arms locked by ears"))
        ),
        ExerciseEntity(
            id = "ex_hanging_leg_raises",
            name = "Hanging Straight Leg Raises",
            category = "Core",
            progressionTier = 3,
            targetSets = 3,
            targetRepsOrDuration = "8 - 12 reps",
            isDurationBased = false,
            muscleGroup = "Lower Abs & Hip Flexors",
            equipmentRequired = "Pull-up Bar",
            shortInstructions = "Hang with active shoulders, curl pelvis up and lift straight legs until toes touch bar.",
            formCuesJson = listToJson(listOf("No swinging momentum", "Slow 2s negative", "Exhale on lift"))
        ),
        ExerciseEntity(
            id = "ex_dragon_flag",
            name = "Dragon Flag Negatives",
            category = "Core",
            progressionTier = 4,
            targetSets = 3,
            targetRepsOrDuration = "5 slow negatives",
            isDurationBased = false,
            muscleGroup = "Entire Core & Lats",
            equipmentRequired = "Bench or Sturdy Pole",
            shortInstructions = "Lie on bench grasping edge behind head. Lift entire torso and legs as a straight unit, lower under extreme core control.",
            formCuesJson = listToJson(listOf("Body straight as a board", "No piking at hips", "Resist gravity slowly"))
        ),

        // SKILLS
        ExerciseEntity(
            id = "ex_chest_to_wall_hs",
            name = "Chest-to-Wall Handstand Hold",
            category = "Skill",
            progressionTier = 2,
            targetSets = 3,
            targetRepsOrDuration = "30 - 45s hold",
            isDurationBased = true,
            defaultDurationSeconds = 30,
            muscleGroup = "Shoulders & Balance",
            equipmentRequired = "Wall",
            shortInstructions = "Walk feet up wall with hands 6-12 inches away. Push shoulders upward to ears and squeeze ribs in.",
            formCuesJson = listToJson(listOf("Look at thumbs", "Grip floor with fingertips", "Point toes"))
        ),
        ExerciseEntity(
            id = "ex_tuck_lsit",
            name = "Tuck L-sit Hold",
            category = "Skill",
            progressionTier = 2,
            targetSets = 3,
            targetRepsOrDuration = "20 - 30s hold",
            isDurationBased = true,
            defaultDurationSeconds = 25,
            muscleGroup = "Core & Scapular Depression",
            equipmentRequired = "Parallettes / Floor",
            shortInstructions = "Press hands down with locked arms, push shoulders away from ears, pull tucked knees up to chest.",
            formCuesJson = listToJson(listOf("Straight arms", "Hips level with hands", "Breathe steadily"))
        ),
        ExerciseEntity(
            id = "ex_planche_lean",
            name = "Planche Lean Hold",
            category = "Skill",
            progressionTier = 2,
            targetSets = 4,
            targetRepsOrDuration = "20 - 30s hold",
            isDurationBased = true,
            defaultDurationSeconds = 25,
            muscleGroup = "Anterior Delts & Bicep Tendons",
            equipmentRequired = "Floor / Parallettes",
            shortInstructions = "In push-up position, protract shoulder blades into a dome and lean shoulders forward past wrists.",
            formCuesJson = listToJson(listOf("Arms locked straight", "Tuck pelvis", "Lean as far forward as possible"))
        ),
        ExerciseEntity(
            id = "ex_tuck_front_lever",
            name = "Tuck Front Lever Hold",
            category = "Skill",
            progressionTier = 3,
            targetSets = 3,
            targetRepsOrDuration = "15 - 25s hold",
            isDurationBased = true,
            defaultDurationSeconds = 20,
            muscleGroup = "Lats & Core",
            equipmentRequired = "Pull-up Bar / Rings",
            shortInstructions = "Hang with locked straight arms and pull body into horizontal alignment with knees tucked to chest.",
            formCuesJson = listToJson(listOf("Arms locked straight", "Depress scapulae", "Torso horizontal"))
        )
    )
}
