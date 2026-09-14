package com.example.data.model

data class Exercise(
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

data class DayProgram(
    val dayName: String, // "Monday", "Tuesday", etc.
    val workoutName: String,
    val isRestDay: Boolean,
    val exercises: List<Exercise>,
    val focusDescription: String
)

data class SkillLevel(
    val level: Int,
    val title: String,
    val targetStandard: String, // e.g. "30s Hold", "3 x 10 Reps"
    val description: String,
    val techniqueCues: List<String>,
    val commonMistakes: String
)

data class SkillProgression(
    val id: String, // "handstand", "lsit", "planche"
    val name: String,
    val shortDescription: String,
    val levels: List<SkillLevel>
)

object CalisthenicsData {

    val WeeklySchedule: List<DayProgram> = listOf(
        DayProgram(
            dayName = "Monday",
            workoutName = "Push + Handstand",
            isRestDay = false,
            focusDescription = "Chest, shoulders, triceps pushing power and overhead balance foundations.",
            exercises = listOf(
                Exercise(
                    id = "mon_1",
                    name = "Pike Push-ups",
                    targetSets = 3,
                    targetRepsOrDuration = "8 - 10 reps",
                    shortInstructions = "Hips high in an inverted V. Lower your head forward between your hands in a tripod shape and press up through shoulders.",
                    formCues = listOf("Keep legs straight", "Forehead touches ahead of hands", "Push ground away at top lockout"),
                    muscleGroup = "Shoulders & Triceps"
                ),
                Exercise(
                    id = "mon_2",
                    name = "Standard / Diamond Push-ups",
                    targetSets = 3,
                    targetRepsOrDuration = "10 - 15 reps",
                    shortInstructions = "Full range push-up. Squeeze glutes and abs tight. Lower until chest grazes floor, elbows tucked at 45 degrees.",
                    formCues = listOf("Avoid sagging lower back", "Full arm extension at top", "Slow 2-second negative descent"),
                    muscleGroup = "Chest & Triceps"
                ),
                Exercise(
                    id = "mon_3",
                    name = "Chest-to-Wall Handstand Hold",
                    targetSets = 3,
                    targetRepsOrDuration = "30 - 45s hold",
                    shortInstructions = "Walk feet up wall with hands 6-12 inches away. Actively push shoulders upward to your ears and squeeze ribs in.",
                    formCues = listOf("Look at your thumbs", "Grip floor with fingertips (spider fingers)", "Point your toes toward ceiling"),
                    muscleGroup = "Shoulder Stability & Core",
                    isDurationBased = true,
                    defaultDurationSeconds = 30
                ),
                Exercise(
                    id = "mon_4",
                    name = "Tricep Dips on Bench / Bar",
                    targetSets = 3,
                    targetRepsOrDuration = "10 - 12 reps",
                    shortInstructions = "Lower under control until upper arms are parallel to floor. Press vigorously upward through palms to lock out.",
                    formCues = listOf("Elbows pointing straight back", "Keep shoulders depressed", "Control the tempo"),
                    muscleGroup = "Triceps"
                ),
                Exercise(
                    id = "mon_5",
                    name = "Wrist & Shoulder Mobility Cooldown",
                    targetSets = 2,
                    targetRepsOrDuration = "45s active flow",
                    shortInstructions = "On hands and knees, rotate wrists through 360 degrees and gently shift bodyweight to decompress joints.",
                    formCues = listOf("Gentle steady breathing", "No sharp pain", "Smooth circular rocking"),
                    muscleGroup = "Mobility & Recovery",
                    isDurationBased = true,
                    defaultDurationSeconds = 45
                )
            )
        ),
        DayProgram(
            dayName = "Tuesday",
            workoutName = "Pull + L-sit",
            isRestDay = false,
            focusDescription = "Back, lats, biceps pulling power and compressed abdominal core.",
            exercises = listOf(
                Exercise(
                    id = "tue_1",
                    name = "Pull-ups / Chin-ups",
                    targetSets = 4,
                    targetRepsOrDuration = "6 - 10 reps",
                    shortInstructions = "Dead hang start. Drive elbows straight down toward your hips. Clear your chin above the bar with zero kipping.",
                    formCues = listOf("Engage scapulae first", "Chest touches bar at peak", "Controlled 2-second descent"),
                    muscleGroup = "Lats & Biceps"
                ),
                Exercise(
                    id = "tue_2",
                    name = "Inverted Bodyweight Rows",
                    targetSets = 3,
                    targetRepsOrDuration = "10 - 12 reps",
                    shortInstructions = "Hang beneath bar or rings with body in a straight plank. Pull chest all the way to bar, squeezing shoulder blades.",
                    formCues = listOf("Keep glutes clamped", "Elbows drive backward", "Pause 1s at top contraction"),
                    muscleGroup = "Upper Back & Rear Delts"
                ),
                Exercise(
                    id = "tue_3",
                    name = "Tuck / Full L-sit Hold",
                    targetSets = 3,
                    targetRepsOrDuration = "20 - 30s hold",
                    shortInstructions = "Depress shoulders forcefully downward on parallel bars or floor. Lift hips and pull knees/legs up at 90 degrees.",
                    formCues = listOf("Straight locked elbows", "Hips in line with hands", "Point toes forward"),
                    muscleGroup = "Hip Flexors & Core",
                    isDurationBased = true,
                    defaultDurationSeconds = 25
                ),
                Exercise(
                    id = "tue_4",
                    name = "Active Bar Hang & Scapular Pulls",
                    targetSets = 3,
                    targetRepsOrDuration = "30s hold + 8 pulls",
                    shortInstructions = "Hang from bar, alternate between passive hang and active scapular depression without bending elbows.",
                    formCues = listOf("Arms remain straight", "Pull shoulder blades down and back", "Build resilient forearms"),
                    muscleGroup = "Grip & Scapular Control",
                    isDurationBased = true,
                    defaultDurationSeconds = 30
                )
            )
        ),
        DayProgram(
            dayName = "Wednesday",
            workoutName = "Rest & Recovery",
            isRestDay = true,
            focusDescription = "Central nervous system recovery, tendon rejuvenation, and light joint mobility.",
            exercises = emptyList()
        ),
        DayProgram(
            dayName = "Thursday",
            workoutName = "Legs + Core",
            isRestDay = false,
            focusDescription = "Lower body unilateral power, deep knee flexion, and anti-extension core control.",
            exercises = listOf(
                Exercise(
                    id = "thu_1",
                    name = "Deep Bodyweight Squats / Pistol Progressions",
                    targetSets = 4,
                    targetRepsOrDuration = "15 - 20 reps",
                    shortInstructions = "Feet shoulder-width apart. Drop hips deeply past parallel with upright chest. Drive evenly through whole foot.",
                    formCues = listOf("Knees tracking over second toe", "Chest upright", "Full hip extension at top"),
                    muscleGroup = "Quads & Glutes"
                ),
                Exercise(
                    id = "thu_2",
                    name = "Walking Lunges",
                    targetSets = 3,
                    targetRepsOrDuration = "12 reps per leg",
                    shortInstructions = "Step forward with tall posture. Back knee lightly taps ground with 90 degree angles in both legs.",
                    formCues = listOf("Torso vertical", "Do not let front knee cave inward", "Explode out of front foot"),
                    muscleGroup = "Hamstrings & Balance"
                ),
                Exercise(
                    id = "thu_3",
                    name = "Hollow Body Hold",
                    targetSets = 3,
                    targetRepsOrDuration = "30 - 45s hold",
                    shortInstructions = "Lie on back. Flatten lumbar spine completely into floor. Raise arms and straight legs so body forms a shallow banana curve.",
                    formCues = listOf("Zero gap between lower back and floor", "Point toes", "Arms locked overhead"),
                    muscleGroup = "Deep Core & Pelvic Floor",
                    isDurationBased = true,
                    defaultDurationSeconds = 35
                ),
                Exercise(
                    id = "thu_4",
                    name = "Hanging Knee / Straight Leg Raises",
                    targetSets = 3,
                    targetRepsOrDuration = "10 - 12 reps",
                    shortInstructions = "Hang with active shoulders. Curl pelvis upward, raising legs without swinging momentum. Lower under strict control.",
                    formCues = listOf("Avoid back arch swing", "Exhale on lift", "Control the negative phase"),
                    muscleGroup = "Lower Abs & Hip Flexors"
                ),
                Exercise(
                    id = "thu_5",
                    name = "Single-Leg Calf Raises",
                    targetSets = 3,
                    targetRepsOrDuration = "15 reps per leg",
                    shortInstructions = "Stand on ledge. Drop heel for full calf stretch, then press high onto big toe ball with a 1-second squeeze.",
                    formCues = listOf("Full ankle range of motion", "Pause at top", "Smooth tempo"),
                    muscleGroup = "Calves & Ankle Stability"
                )
            )
        ),
        DayProgram(
            dayName = "Friday",
            workoutName = "Push + Planche",
            isRestDay = false,
            focusDescription = "Straight-arm shoulder leverage, scapular protraction, and dip power.",
            exercises = listOf(
                Exercise(
                    id = "fri_1",
                    name = "Parallel Bar Dips",
                    targetSets = 4,
                    targetRepsOrDuration = "8 - 12 reps",
                    shortInstructions = "Lean slightly forward. Lower until shoulders dip slightly below elbow crease, then press firmly to full lockout.",
                    formCues = listOf("Shoulders packed down away from ears", "Controlled descent", "Lock out elbows cleanly"),
                    muscleGroup = "Chest, Triceps & Front Delts"
                ),
                Exercise(
                    id = "fri_2",
                    name = "Planche Lean Hold",
                    targetSets = 4,
                    targetRepsOrDuration = "20 - 30s hold",
                    shortInstructions = "In top pushup position, turn fingers slightly outward. Protract shoulder blades fully into a dome and lean shoulders forward past wrists.",
                    formCues = listOf("Lock elbows rock straight", "Tuck pelvis (posterior tilt)", "Lean weight as far as comfortable"),
                    muscleGroup = "Bicep Tendons & Anterior Delts",
                    isDurationBased = true,
                    defaultDurationSeconds = 25
                ),
                Exercise(
                    id = "fri_3",
                    name = "Pseudo Planche Push-ups",
                    targetSets = 3,
                    targetRepsOrDuration = "6 - 10 reps",
                    shortInstructions = "Maintain the forward planche lean throughout the entire push-up repetition. Keep hips tucked and scapulae protracting at top.",
                    formCues = listOf("Hands placed by lower ribs", "Stay forward during descent", "High core tension"),
                    muscleGroup = "Shoulders & Core"
                ),
                Exercise(
                    id = "fri_4",
                    name = "Crow Pose / Frog Stand Balance",
                    targetSets = 3,
                    targetRepsOrDuration = "25 - 40s hold",
                    shortInstructions = "Place knees on back of upper triceps. Lean body forward into palms until feet gently float off ground. Balance on hands.",
                    formCues = listOf("Grip mat with fingertips", "Gaze slightly ahead, not down", "Find balance equilibrium point"),
                    muscleGroup = "Hand Balance & Shoulders",
                    isDurationBased = true,
                    defaultDurationSeconds = 30
                )
            )
        ),
        DayProgram(
            dayName = "Saturday",
            workoutName = "Pull + Legs + Core",
            isRestDay = false,
            focusDescription = "Full-body athletic integration combining vertical pulling, unilateral leg strength, and rotational core.",
            exercises = listOf(
                Exercise(
                    id = "sat_1",
                    name = "Wide Grip / Archer Pull-ups",
                    targetSets = 4,
                    targetRepsOrDuration = "6 - 8 reps",
                    shortInstructions = "Grip bar wider than shoulder-width. Pull with explosive intent, driving elbows into lats. Avoid kicking legs.",
                    formCues = listOf("Initiate with scapular pull", "Steady tempo", "Full extension at bottom"),
                    muscleGroup = "Lats & Upper Back"
                ),
                Exercise(
                    id = "sat_2",
                    name = "Bulgarian Split Squats",
                    targetSets = 3,
                    targetRepsOrDuration = "10 reps per leg",
                    shortInstructions = "Rear foot elevated on bench or chair. Lower front hip until thigh is parallel to ground. Drive through front heel.",
                    formCues = listOf("Keep torso slightly hinged", "Feel quad and glute working", "Stable balanced stance"),
                    muscleGroup = "Quads & Glutes"
                ),
                Exercise(
                    id = "sat_3",
                    name = "L-sit Kickouts / Tucks on Bars",
                    targetSets = 3,
                    targetRepsOrDuration = "8 - 10 reps",
                    shortInstructions = "Hold support position on bars. Alternately extend legs straight out into L-sit, hold for 1 second, and tuck back in.",
                    formCues = listOf("Push shoulders down", "Lock triceps", "Legs parallel to ground on kickout"),
                    muscleGroup = "Core & Hip Flexors"
                ),
                Exercise(
                    id = "sat_4",
                    name = "Prone Superman / Arch Hold",
                    targetSets = 3,
                    targetRepsOrDuration = "30 - 45s hold",
                    shortInstructions = "Lie flat on stomach. Contract glutes and posterior chain to lift chest, arms, and straight legs together off floor.",
                    formCues = listOf("Look at ground to protect neck", "Squeeze glutes hard", "Reach long from fingers to toes"),
                    muscleGroup = "Spinal Erectors & Posterior Chain",
                    isDurationBased = true,
                    defaultDurationSeconds = 35
                )
            )
        ),
        DayProgram(
            dayName = "Sunday",
            workoutName = "Rest & Reset",
            isRestDay = true,
            focusDescription = "Complete systemic rest. Recharge glycogen, hydrate, sleep well, and prepare for the upcoming training week.",
            exercises = emptyList()
        )
    )

    val SkillProgressions: List<SkillProgression> = listOf(
        SkillProgression(
            id = "handstand",
            name = "Handstand",
            shortDescription = "The crown jewel of overhead balance and straight-arm pressing.",
            levels = listOf(
                SkillLevel(
                    level = 1,
                    title = "Pike Push-ups & Pike Hold",
                    targetStandard = "30s Hold or 3x10 Reps",
                    description = "Develop vertical pushing shoulder strength and comfort being inverted.",
                    techniqueCues = listOf(
                        "Form sharp 90-degree angle at hips with feet elevated if possible",
                        "Look slightly forward at hands, not at feet",
                        "Push shoulder blades actively toward your ears"
                    ),
                    commonMistakes = "Allowing elbows to flare wide instead of 45-degree angle; bending at knees."
                ),
                SkillLevel(
                    level = 2,
                    title = "Chest-to-Wall Handstand Hold",
                    targetStandard = "45s Continuous Hold",
                    description = "Build authentic straight-line alignment without the banana back curvature.",
                    techniqueCues = listOf(
                        "Walk feet up wall, hands 6-12 inches from wall",
                        "Only toes and nose contact the wall lightly",
                        "Squeeze glutes and tuck pelvis to eliminate lower back arch"
                    ),
                    commonMistakes = "Facing away from wall too early; losing active shoulder push."
                ),
                SkillLevel(
                    level = 3,
                    title = "Wall Balance Heel / Toe Taps",
                    targetStandard = "30s Active Balance Control",
                    description = "Learn to use fingertip pressure (cambré) to pull off the wall into free balance.",
                    techniqueCues = listOf(
                        "From chest-to-wall or back-to-wall, press fingertips to bring feet off wall",
                        "Catch balance with finger flexion",
                        "Float both feet together in middle air"
                    ),
                    commonMistakes = "Kicking hard away from wall instead of pressing with fingertips."
                ),
                SkillLevel(
                    level = 4,
                    title = "Freestanding Kick-up & Hold",
                    targetStandard = "15 - 20s Clean Hold",
                    description = "Consistent kick-up entry with controlled deceleration at vertical.",
                    techniqueCues = listOf(
                        "Reach hands to floor before kicking back leg",
                        "Kick with lead leg while trail leg meets it smoothly",
                        "Lock elbows completely before feet leave floor"
                    ),
                    commonMistakes = "Over-kicking and falling over; soft bent elbows."
                ),
                SkillLevel(
                    level = 5,
                    title = "Solid Freestanding Handstand",
                    targetStandard = "45s+ Flawless Straight Line",
                    description = "Full mastery with micro-corrections, effortless shapes, and rock-solid endurance.",
                    techniqueCues = listOf(
                        "Perfect stacked line: wrists, shoulders, hips, ankles",
                        "Breathe calmly into diaphragm",
                        "Active shoulder elevation throughout entire duration"
                    ),
                    commonMistakes = "Holding breath; relaxing shoulder elevation as fatigue sets in."
                )
            )
        ),
        SkillProgression(
            id = "lsit",
            name = "L-sit",
            shortDescription = "Ultimate test of compressed core, scapular depression, and tricep support.",
            levels = listOf(
                SkillLevel(
                    level = 1,
                    title = "Tuck Support Hold on Bars / Floor",
                    targetStandard = "30s Solid Hold",
                    description = "Condition straight arm support and forceful scapular depression.",
                    techniqueCues = listOf(
                        "Press palms down with locked elbows",
                        "Push shoulders as far down from ears as possible",
                        "Tuck knees to chest with feet hovering above floor"
                    ),
                    commonMistakes = "Letting shoulders shrug up by ears; bending arms."
                ),
                SkillLevel(
                    level = 2,
                    title = "One-Leg Extended L-sit Hold",
                    targetStandard = "20s per Leg",
                    description = "Lengthen lever arm on one side while keeping hips lifted.",
                    techniqueCues = listOf(
                        "Extend one leg completely straight with pointed toe",
                        "Keep opposite knee tucked close to chest",
                        "Alternate legs smoothly between sets"
                    ),
                    commonMistakes = "Letting hips sink behind the hands."
                ),
                SkillLevel(
                    level = 3,
                    title = "Full Tuck L-sit",
                    targetStandard = "30s Hold",
                    description = "Both knees elevated tightly with hips pushed forward in line with wrists.",
                    techniqueCues = listOf(
                        "Compress thighs tight to abdomen",
                        "Push down hard on parallettes or floor",
                        "Maintain steady breathing"
                    ),
                    commonMistakes = "Dropping knees below 90 degrees; leaning torso too far back."
                ),
                SkillLevel(
                    level = 4,
                    title = "Full L-sit on Parallettes / Floor",
                    targetStandard = "15 - 30s Horizontal Hold",
                    description = "Both legs locked completely straight, parallel to ground.",
                    techniqueCues = listOf(
                        "Quads flexed rock hard to keep knees straight",
                        "Toes pointed forward",
                        "Hips stay slightly in front of or directly between palms"
                    ),
                    commonMistakes = "Quads cramping due to insufficient warm-up; bending knees."
                ),
                SkillLevel(
                    level = 5,
                    title = "High L-sit to V-sit Progression",
                    targetStandard = "15s+ Elevated Angle",
                    description = "Elite compression pulling toes above hip height toward a 45-degree V.",
                    techniqueCues = listOf(
                        "Extreme active compression pulling chest toward knees",
                        "Hips push forward while torso leans slightly back",
                        "Lock triceps with max tension"
                    ),
                    commonMistakes = "Bending knees to get feet higher; losing shoulder depression."
                )
            )
        ),
        SkillProgression(
            id = "planche",
            name = "Planche",
            shortDescription = "The Holy Grail of straight-arm horizontal pushing leverage.",
            levels = listOf(
                SkillLevel(
                    level = 1,
                    title = "Planche Lean Hold",
                    targetStandard = "30s Hold at 45° Lean",
                    description = "Condition bicep distal tendons, wrists, and serratus anterior for heavy forward load.",
                    techniqueCues = listOf(
                        "Turn hands slightly outward to protect wrists",
                        "Protract shoulder blades into a rounded upper back dome",
                        "Lean shoulders forward until wrists feel significant load"
                    ),
                    commonMistakes = "Arching lower back; bending elbows even slightly."
                ),
                SkillLevel(
                    level = 2,
                    title = "Frog Stand / Crow Pose",
                    targetStandard = "30s Balanced Hold",
                    description = "Balance forward bodyweight onto palms with knees resting on triceps.",
                    techniqueCues = listOf(
                        "Place knees right behind your elbows/triceps",
                        "Lean forward smoothly until feet lift with zero jumping",
                        "Engage fingers into ground for micro-balance"
                    ),
                    commonMistakes = "Kicking feet off floor instead of smooth weight transition."
                ),
                SkillLevel(
                    level = 3,
                    title = "Tuck Planche Hold",
                    targetStandard = "15 - 20s Hold on Bars",
                    description = "Knees tucked into chest with zero body contact on arms. Straight arms!",
                    techniqueCues = listOf(
                        "Maximal scapular protraction (push ground away)",
                        "Elevate hips up to shoulder level",
                        "Elbow pits turned forward (straight arms locked)"
                    ),
                    commonMistakes = "Bending elbows to cheat leverage; hips sagging below shoulders."
                ),
                SkillLevel(
                    level = 4,
                    title = "Advanced Tuck Planche",
                    targetStandard = "15s Clean Hold",
                    description = "Back opens up to a 90-degree angle with knees behind hips.",
                    techniqueCues = listOf(
                        "Open hips so thighs are perpendicular to torso",
                        "Hips strictly level with shoulders",
                        "Intense forward shoulder lean to balance longer lever"
                    ),
                    commonMistakes = "Rounding lower back too much; dropping hip height."
                ),
                SkillLevel(
                    level = 5,
                    title = "Straddle / Full Planche",
                    targetStandard = "8 - 15s Level Hold",
                    description = "Body parallel to ground in complete straight-arm suspension.",
                    techniqueCues = listOf(
                        "Legs fully extended, toes pointed",
                        "Posterior pelvic tilt with clamped glutes",
                        "Shoulders forward, arms like solid steel pillars"
                    ),
                    commonMistakes = "Bent arms; piked hips; holding breath under extreme load."
                )
            )
        ),
        SkillProgression(
            id = "front_lever",
            name = "Front Lever",
            shortDescription = "The supreme display of horizontal pulling power and lat engagement.",
            levels = listOf(
                SkillLevel(
                    level = 1,
                    title = "Tuck Front Lever Hold",
                    targetStandard = "25 - 30s Solid Hold",
                    description = "Hang with straight arms and elevate body horizontally with knees pulled tightly to chest.",
                    techniqueCues = listOf(
                        "Lock elbows completely straight",
                        "Pull shoulder blades down and depress scapulae",
                        "Drive straight arms downward into the bar like a straight-arm pulldown"
                    ),
                    commonMistakes = "Bending elbows to compensate for weak lats; hips sagging below shoulders."
                ),
                SkillLevel(
                    level = 2,
                    title = "Advanced Tuck Front Lever",
                    targetStandard = "20s Clean Hold",
                    description = "Open hip angle to 90 degrees with flat back parallel to ground.",
                    techniqueCues = listOf(
                        "Push knees away until thighs form 90-degree angle to torso",
                        "Maintain flat upper and lower back",
                        "Intense lat engagement pulling the bar toward hips"
                    ),
                    commonMistakes = "Rounding thoracic spine; letting knees creep back into tight tuck."
                ),
                SkillLevel(
                    level = 3,
                    title = "One-Leg Front Lever",
                    targetStandard = "15s per Leg",
                    description = "Extend one leg fully straight while keeping opposite knee tucked.",
                    techniqueCues = listOf(
                        "Extend working leg completely straight with pointed toes",
                        "Hips stay square and perfectly level with shoulders",
                        "Alternate legs across sets"
                    ),
                    commonMistakes = "Rotating hips to one side; relaxing tension on the tucked leg."
                ),
                SkillLevel(
                    level = 4,
                    title = "Straddle Front Lever",
                    targetStandard = "10 - 15s Horizontal Hold",
                    description = "Both legs extended fully in a wide straddle, reducing the lever length.",
                    techniqueCues = listOf(
                        "Spread legs as wide as possible with locked knees",
                        "Squeeze glutes to keep pelvis in line with shoulders",
                        "Head in neutral position looking toward toes"
                    ),
                    commonMistakes = "Piking hips up toward ceiling; bending elbows."
                ),
                SkillLevel(
                    level = 5,
                    title = "Full Front Lever",
                    targetStandard = "10s+ Flawless Straight Line",
                    description = "Full horizontal body suspension parallel to the ground with legs together.",
                    techniqueCues = listOf(
                        "Ankles, knees, hips, and shoulders form a dead-straight horizontal line",
                        "Maximum lat activation pulling down against the bar",
                        "Glutes and abs fully contracted"
                    ),
                    commonMistakes = "Sagging hips; loose legs; bent arms."
                )
            )
        ),
        SkillProgression(
            id = "muscle_up",
            name = "Muscle-up",
            shortDescription = "The iconic gymnastics milestone blending explosive pull with a deep bar dip.",
            levels = listOf(
                SkillLevel(
                    level = 1,
                    title = "Explosive High Pull-ups",
                    targetStandard = "3 Sets of 5 Reps to Sternum",
                    description = "Build the explosive vertical pulling trajectory required to clear the bar.",
                    techniqueCues = listOf(
                        "Pull with explosive intent driving elbows down and back",
                        "Aim to touch lower chest or sternum to bar",
                        "Keep legs tight together with zero kicking"
                    ),
                    commonMistakes = "Pulling only to chin; slow pull speed lacking explosive drive."
                ),
                SkillLevel(
                    level = 2,
                    title = "Straight Bar Dips",
                    targetStandard = "3 Sets of 10 Clean Reps",
                    description = "Master the pressing out phase with the bar positioned across your hips/waist.",
                    techniqueCues = listOf(
                        "Lean chest over the bar as you lower",
                        "Dip deep until chest touches or clears below bar level",
                        "Press up powerfully to full lockout"
                    ),
                    commonMistakes = "Failing to reach full depth; legs swinging wildly."
                ),
                SkillLevel(
                    level = 3,
                    title = "Negative Muscle-ups & False Grip",
                    targetStandard = "5 Reps with 5s Slow Negatives",
                    description = "Control the crucial transition zone from above the bar down to dead hang.",
                    techniqueCues = listOf(
                        "Start at top support, lower slowly to chest level",
                        "Trace chest close to bar through transition arc",
                        "Control descent all the way to dead hang over 5 seconds"
                    ),
                    commonMistakes = "Dropping rapidly through the transition point without resisting."
                ),
                SkillLevel(
                    level = 4,
                    title = "Banded / Light Kipping Muscle-up",
                    targetStandard = "3 Sets of 3 Clean Reps",
                    description = "Execute the full dynamic movement pattern and fast wrist rollover.",
                    techniqueCues = listOf(
                        "Slight hollow swing, then pull in a C-shaped curve around bar",
                        "Aggressively throw head and chest over bar at peak height",
                        "Rotate wrists over the top simultaneously"
                    ),
                    commonMistakes = "Chicken-winging (one elbow rolling over before the other)."
                ),
                SkillLevel(
                    level = 5,
                    title = "Strict Bar / Ring Muscle-up",
                    targetStandard = "3 to 5 Strict Reps (No Kip)",
                    description = "Pure upper body pulling and pressing power with zero momentum.",
                    techniqueCues = listOf(
                        "Dead hang start with active false grip or high wrist placement",
                        "Powerful vertical pull followed by synchronized smooth transition",
                        "Press out to clean lockout with quiet, motionless legs"
                    ),
                    commonMistakes = "Using knee drive; uneven elbow turnover."
                )
            )
        )
    )

    val DefaultPersonalRecords: List<PersonalRecordData> = listOf(
        PersonalRecordData("pull_ups", "Pull-ups", 10, "reps", "reps"),
        PersonalRecordData("dips", "Parallel Dips", 15, "reps", "reps"),
        PersonalRecordData("push_ups", "Push-ups", 25, "reps", "reps"),
        PersonalRecordData("pike_pushups", "Pike Push-ups", 12, "reps", "reps"),
        PersonalRecordData("squats", "Bodyweight Squats", 35, "reps", "reps"),
        PersonalRecordData("handstand_hold", "Handstand Hold", 20, "s", "hold"),
        PersonalRecordData("lsit_hold", "L-sit Hold", 15, "s", "hold"),
        PersonalRecordData("planche_lean", "Planche Lean Hold", 30, "s", "hold"),
        PersonalRecordData("tuck_planche", "Tuck Planche Hold", 8, "s", "hold"),
        PersonalRecordData("front_lever_hold", "Front Lever Hold", 5, "s", "hold"),
        PersonalRecordData("muscle_ups", "Strict Muscle-ups", 1, "reps", "reps")
    )
}

data class PersonalRecordData(
    val key: String,
    val name: String,
    val defaultValue: Int,
    val unit: String,
    val category: String
)
