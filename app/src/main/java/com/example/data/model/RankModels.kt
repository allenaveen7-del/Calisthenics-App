package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class RankTier(
    val tierName: String,
    val division: String,
    val minXp: Int,
    val maxXp: Int,
    val primaryColor: Color,
    val accentColor: Color,
    val emblemName: String
) {
    BRONZE_I("Bronze", "I", 0, 399, Color(0xFFCD7F32), Color(0xFF8D5524), "Shield"),
    BRONZE_II("Bronze", "II", 400, 699, Color(0xFFCD7F32), Color(0xFF8D5524), "Shield"),
    BRONZE_III("Bronze", "III", 700, 999, Color(0xFFCD7F32), Color(0xFF8D5524), "Shield"),

    SILVER_I("Silver", "I", 1000, 1499, Color(0xFFC0C0C0), Color(0xFF94A3B8), "Iron Shield"),
    SILVER_II("Silver", "II", 1500, 1999, Color(0xFFC0C0C0), Color(0xFF94A3B8), "Iron Shield"),
    SILVER_III("Silver", "III", 2000, 2499, Color(0xFFC0C0C0), Color(0xFF94A3B8), "Iron Shield"),

    GOLD_I("Gold", "I", 2500, 3099, Color(0xFFFFD700), Color(0xFFB8860B), "Gilded Eagle"),
    GOLD_II("Gold", "II", 3100, 3799, Color(0xFFFFD700), Color(0xFFB8860B), "Gilded Eagle"),
    GOLD_III("Gold", "III", 3800, 4499, Color(0xFFFFD700), Color(0xFFB8860B), "Gilded Eagle"),

    PLATINUM_I("Platinum", "I", 4500, 5199, Color(0xFF00E5FF), Color(0xFF00838F), "Cyan Blade"),
    PLATINUM_II("Platinum", "II", 5200, 5899, Color(0xFF00E5FF), Color(0xFF00838F), "Cyan Blade"),
    PLATINUM_III("Platinum", "III", 5900, 6699, Color(0xFF00E5FF), Color(0xFF00838F), "Cyan Blade"),
    PLATINUM_IV("Platinum", "IV", 6700, 7499, Color(0xFF00E5FF), Color(0xFF00838F), "Cyan Blade"),

    DIAMOND_I("Diamond", "I", 7500, 8399, Color(0xFF38BDF8), Color(0xFF0284C7), "Diamond Falcon"),
    DIAMOND_II("Diamond", "II", 8400, 9299, Color(0xFF38BDF8), Color(0xFF0284C7), "Diamond Falcon"),
    DIAMOND_III("Diamond", "III", 9300, 10199, Color(0xFF38BDF8), Color(0xFF0284C7), "Diamond Falcon"),
    DIAMOND_IV("Diamond", "IV", 10200, 11099, Color(0xFF38BDF8), Color(0xFF0284C7), "Diamond Falcon"),
    DIAMOND_V("Diamond", "V", 11100, 11999, Color(0xFF38BDF8), Color(0xFF0284C7), "Diamond Falcon"),

    CROWN_MASTER("Crown", "Master", 12000, 17999, Color(0xFFE040FB), Color(0xFF7B1FA2), "Royal Crown"),
    ACE_GRANDMASTER("Ace", "Grandmaster", 18000, 24999, Color(0xFFFF1744), Color(0xFFD50000), "Apex Ace"),
    TITAN_CONQUEROR("Titan", "Conqueror", 25000, 999999, Color(0xFF00FF88), Color(0xFF00E5FF), "Titan Apex")
}

data class RankProgressData(
    val currentTier: RankTier,
    val nextTier: RankTier?,
    val currentXp: Int,
    val xpInCurrentTier: Int,
    val xpRequiredForNextTier: Int,
    val progressRatio: Float,
    val workoutsXp: Int,
    val skillsXp: Int,
    val prsXp: Int,
    val streakXp: Int,
    val globalLeaderboardRank: Int = 84
)

object RankCalculator {
    fun calculateRank(
        workoutCount: Int,
        skillLevelsSum: Int,
        prCount: Int,
        streakDays: Int
    ): RankProgressData {
        val workoutsXp = workoutCount * 150
        val skillsXp = skillLevelsSum * 250
        val prsXp = prCount * 75
        val streakXp = streakDays * 100
        val totalXp = workoutsXp + skillsXp + prsXp + streakXp

        val tiers = RankTier.values()
        val currentTier = tiers.lastOrNull { totalXp >= it.minXp } ?: RankTier.BRONZE_I
        val currentIndex = tiers.indexOf(currentTier)
        val nextTier = if (currentIndex < tiers.size - 1) tiers[currentIndex + 1] else null

        val tierSpan = if (nextTier != null) (nextTier.minXp - currentTier.minXp) else 5000
        val xpInTier = totalXp - currentTier.minXp
        val progressRatio = if (nextTier != null && tierSpan > 0) {
            (xpInTier.toFloat() / tierSpan.toFloat()).coerceIn(0f, 1f)
        } else {
            1.0f
        }

        // Leaderboard position estimation based on XP
        val rankPos = when {
            totalXp > 25000 -> (1..20).random()
            totalXp > 18000 -> (21..50).random()
            totalXp > 12000 -> (51..120).random()
            totalXp > 7500 -> (121..350).random()
            totalXp > 4500 -> (351..950).random()
            else -> 1240 - (totalXp / 10).coerceAtMost(1000)
        }

        return RankProgressData(
            currentTier = currentTier,
            nextTier = nextTier,
            currentXp = totalXp,
            xpInCurrentTier = xpInTier,
            xpRequiredForNextTier = tierSpan,
            progressRatio = progressRatio,
            workoutsXp = workoutsXp,
            skillsXp = skillsXp,
            prsXp = prsXp,
            streakXp = streakXp,
            globalLeaderboardRank = rankPos
        )
    }
}

data class LeaderboardUser(
    val rankPosition: Int,
    val name: String,
    val handle: String,
    val rankTier: RankTier,
    val xpScore: Int,
    val workoutsCompleted: Int,
    val streakDays: Int,
    val isCurrentUser: Boolean = false,
    val avatarId: String = "avatar_falcon"
)
