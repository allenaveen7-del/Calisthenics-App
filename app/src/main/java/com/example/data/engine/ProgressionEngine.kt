package com.example.data.engine

import com.example.data.local.PersonalRecord
import com.example.data.local.SkillProgress
import com.example.data.local.UserProfileEntity
import com.example.data.local.WorkoutRecord
import com.example.data.model.InsightType
import com.example.data.model.ProgressionInsight
import com.example.data.model.StudyMeReport
import java.util.concurrent.TimeUnit

object ProgressionEngine {

    fun analyzeProgress(
        profile: UserProfileEntity?,
        records: List<WorkoutRecord>,
        prs: List<PersonalRecord>,
        skills: List<SkillProgress>
    ): List<ProgressionInsight> {
        val insights = mutableListOf<ProgressionInsight>()

        val prMap = prs.associateBy { it.recordKey }

        // 1. Pull-up Progression Check
        val pullUpPr = prMap["pull_ups"]?.recordValue ?: 0
        if (pullUpPr >= 12) {
            insights.add(
                ProgressionInsight(
                    title = "Ready for Archer Pull-ups & Muscle-up",
                    description = "With ${pullUpPr} strict pull-ups logged, your pulling base is solid. You have the prerequisite strength to begin high explosive pull-ups and archer pull-up progressions.",
                    type = InsightType.LEVEL_UP,
                    actionText = "Add Archer Pull-ups"
                )
            )
        } else if (pullUpPr in 8..11) {
            insights.add(
                ProgressionInsight(
                    title = "Pull-up Volume Target",
                    description = "You're at ${pullUpPr} reps. Prioritize 4-5 sets of 6-8 reps with controlled 2-second eccentrics to break the 12-rep milestone.",
                    type = InsightType.TECHNIQUE
                )
            )
        }

        // 2. Push & Dip Progression Check
        val dipPr = prMap["dips"]?.recordValue ?: 0
        val pushUpPr = prMap["push_ups"]?.recordValue ?: 0
        if (dipPr >= 15 && pushUpPr >= 25) {
            insights.add(
                ProgressionInsight(
                    title = "Unlock Straight-Bar Dips & Ring Dips",
                    description = "Logging ${dipPr} parallel dips and ${pushUpPr} push-ups indicates strong anterior deltoid and tricep capacity. Progress to Ring Dips or Straight-Bar Dips for muscle-up transitions.",
                    type = InsightType.LEVEL_UP,
                    actionText = "Upgrade Push Progression"
                )
            )
        }

        // 3. Handstand Hold Progression
        val hsProgress = skills.find { it.skillId == "handstand" }
        val hsHold = hsProgress?.bestHoldSeconds ?: 0
        val hsLevel = hsProgress?.currentLevel ?: 1
        if (hsHold >= 30 && hsLevel < 3) {
            insights.add(
                ProgressionInsight(
                    title = "Advance Handstand to Level 3",
                    description = "You recorded a ${hsHold}s handstand hold! You have mastered the endurance base. Move to Wall Heel/Toe taps to train fingertip balance calibration.",
                    type = InsightType.LEVEL_UP,
                    actionText = "Set Handstand to Level 3"
                )
            )
        } else if (hsHold >= 45 && hsLevel < 4) {
            insights.add(
                ProgressionInsight(
                    title = "Freestanding Kick-up Ready",
                    description = "Solid ${hsHold}s hold logged. You are ready to start freestanding kick-up attempts in open space.",
                    type = InsightType.MILESTONE
                )
            )
        }

        // 4. L-sit Progression Check
        val lsitProgress = skills.find { it.skillId == "lsit" }
        val lsitHold = lsitProgress?.bestHoldSeconds ?: 0
        val lsitLevel = lsitProgress?.currentLevel ?: 1
        if (lsitHold >= 20 && lsitLevel <= 2) {
            insights.add(
                ProgressionInsight(
                    title = "Level Up L-sit Progression",
                    description = "Achieving a ${lsitHold}s hold qualifies you for Full Tuck L-sit or One-Leg Extended holds with hips pushed forward.",
                    type = InsightType.LEVEL_UP,
                    actionText = "Advance L-sit"
                )
            )
        }

        // 5. Planche Lean / Tendon Check
        val plancheProgress = skills.find { it.skillId == "planche" }
        val plancheHold = plancheProgress?.bestHoldSeconds ?: 0
        if (plancheHold >= 25 && (plancheProgress?.currentLevel ?: 1) < 2) {
            insights.add(
                ProgressionInsight(
                    title = "Planche Lean Conditioning Solid",
                    description = "${plancheHold}s lean demonstrates bicep tendon conditioning. Begin tuck planche balance attempts on parallettes.",
                    type = InsightType.LEVEL_UP
                )
            )
        }

        // 6. Deload / Periodization Check
        val currentWeek = profile?.currentWeek ?: 1
        val totalWeeks = profile?.totalWeeksInPhase ?: 8
        if (currentWeek >= 6 && currentWeek <= totalWeeks) {
            insights.add(
                ProgressionInsight(
                    title = "Approaching Phase Deload (Week ${currentWeek} of ${totalWeeks})",
                    description = "You have maintained high intensity for ${currentWeek} weeks. Week ${minOf(currentWeek + 1, totalWeeks)} is recommended as an active deload (reduce sets by 40%, focus on joint mobility).",
                    type = InsightType.DELOAD,
                    actionText = "Plan Deload"
                )
            )
        }

        // 7. General Consistency Milestone
        if (records.size >= 10) {
            insights.add(
                ProgressionInsight(
                    title = "${records.size} Total Sessions Completed",
                    description = "Consistency is the primary driver of neurological adaptation in calisthenics. Your tendons and motor patterns are adapting steadily.",
                    type = InsightType.MILESTONE
                )
            )
        }

        if (insights.isEmpty()) {
            insights.add(
                ProgressionInsight(
                    title = "Training Foundation Building",
                    description = "Execute your scheduled weekly workouts and log your sets to activate personalized adaptive progression insights.",
                    type = InsightType.TECHNIQUE
                )
            )
        }

        return insights
    }

    fun generateStudyMeReport(
        profile: UserProfileEntity?,
        records: List<WorkoutRecord>,
        prs: List<PersonalRecord>,
        skills: List<SkillProgress>,
        streak: Int
    ): StudyMeReport {
        val totalWorkouts = records.size
        val targetDaysPerWeek = profile?.trainingDaysPerWeek ?: 5

        // Adherence calculation based on last 28 days
        val now = System.currentTimeMillis()
        val twentyEightDaysAgo = now - TimeUnit.DAYS.toMillis(28)
        val recentWorkouts = records.filter { it.timestamp >= twentyEightDaysAgo }
        val expectedWorkouts = (targetDaysPerWeek * 4).coerceAtLeast(1)
        val adherenceScore = ((recentWorkouts.size.toFloat() / expectedWorkouts) * 100).toInt().coerceIn(10, 100)

        val prMap = prs.associateBy { it.recordKey }
        val pullUp = prMap["pull_ups"]?.recordValue ?: 0
        val dips = prMap["dips"]?.recordValue ?: 0
        val pushUp = prMap["push_ups"]?.recordValue ?: 0
        val hsHold = skills.find { it.skillId == "handstand" }?.bestHoldSeconds ?: 0

        val strengthAssessment = when {
            pullUp >= 15 && dips >= 20 -> "Advanced Upper Body Relative Strength"
            pullUp >= 10 && dips >= 12 -> "Solid Intermediate Calisthenics Competency"
            pullUp >= 6 && dips >= 8 -> "Developing Intermediate Foundation"
            else -> "Fundamental Neuromuscular Conditioning"
        }

        val skillReadiness = when {
            hsHold >= 30 -> "High Balance & Straight-Arm Overhead Stability"
            hsHold >= 15 -> "Developing Inversion Confidence & Scapular Elevation"
            else -> "Entry Level Straight-Arm Support"
        }

        val strengths = mutableListOf<String>()
        val areasToImprove = mutableListOf<String>()

        if (pullUp >= 10) strengths.add("Vertical pulling strength (${pullUp} pull-ups)")
        else areasToImprove.add("Vertical pulling endurance (target 10 strict pull-ups)")

        if (dips >= 12) strengths.add("Chest & tricep pressing power (${dips} dips)")
        else areasToImprove.add("Parallel bar dip depth and lockout")

        if (pushUp >= 20) strengths.add("Horizontal pushing stamina (${pushUp} push-ups)")
        else areasToImprove.add("Push-up core stability and rep volume")

        if (hsHold >= 20) strengths.add("Inverted balance awareness (${hsHold}s hold)")
        else areasToImprove.add("Wall handstand endurance (aim for 45s continuous)")

        if (streak >= 3) strengths.add("Active workout habit streak of ${streak} days")
        else areasToImprove.add("Weekly schedule adherence consistency")

        val nextPhase = when {
            pullUp >= 12 && dips >= 15 -> "Phase 2: Muscle-up & Dynamic Strength Acceleration"
            pullUp >= 8 -> "Phase 1.5: Straight-Arm Levers & Strict Pulling Hypertrophy"
            else -> "Phase 1: Athletic Foundation & Relative Strength Mastery"
        }

        return StudyMeReport(
            adherenceScore = adherenceScore,
            totalWorkouts = totalWorkouts,
            currentStreak = streak,
            strengthAssessment = strengthAssessment,
            skillReadiness = skillReadiness,
            keyStrengths = strengths.ifEmpty { listOf("Dedication to bodyweight training mastery") },
            areasToImprove = areasToImprove.ifEmpty { listOf("Continue progressive overload across core exercises") },
            recommendedNextPhase = nextPhase
        )
    }
}
