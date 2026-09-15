package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.PersonalRecord
import com.example.data.local.UserProfileEntity
import com.example.data.model.InsightType
import com.example.data.model.ProgressionInsight
import com.example.data.model.StudyMeReport
import com.example.ui.theme.AthleticCyan
import com.example.ui.theme.AthleticLime
import com.example.ui.theme.AthleticOrange
import com.example.ui.viewmodel.CoachViewModel

import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.LinearProgressIndicator
import com.example.ui.screens.OnboardingWizard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: CoachViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val prs by viewModel.personalRecords.collectAsStateWithLifecycle()
    val insights by viewModel.progressionInsights.collectAsStateWithLifecycle()
    val studyReport by viewModel.studyMeReport.collectAsStateWithLifecycle()
    val streak by viewModel.workoutStreak.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val rankData by viewModel.rankProgression.collectAsStateWithLifecycle()

    var selectedSection by remember { mutableIntStateOf(0) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showOnboardingWizard by remember { mutableStateOf(false) }
    var editingPR by remember { mutableStateOf<PersonalRecord?>(null) }
    var showResetDialog by remember { mutableStateOf(false) }

    val sectionTitles = listOf("Rank & Tier", "Identity & Gear", "PR Records", "Progression Insights", "Study Me")

    if (showOnboardingWizard) {
        OnboardingWizard(
            initialLevel = userProfile?.fitnessLevel ?: "Intermediate",
            initialGoal = userProfile?.primaryGoal ?: "Skill Mastery & Relative Strength",
            initialEquipment = userProfile?.equipmentAvailable ?: "Pull-up Bar, Dip Station, Floor, Rings",
            initialDays = userProfile?.trainingDaysPerWeek ?: 5,
            initialWeight = userProfile?.weightKg ?: 74.5f,
            initialHeight = userProfile?.heightCm ?: 178f,
            onDismiss = { showOnboardingWizard = false },
            onFinish = { level, goal, equip, days, weight, height ->
                viewModel.completeOnboarding(
                    fitnessLevel = level,
                    primaryGoal = goal,
                    equipment = equip,
                    daysPerWeek = days,
                    weightKg = weight,
                    heightCm = height
                )
                showOnboardingWizard = false
            }
        )
    }

    if (showEditProfileDialog && userProfile != null) {
        EditProfileDialog(
            profile = userProfile!!,
            onDismiss = { showEditProfileDialog = false },
            onSave = { level, exp, pGoal, sGoal, equip, days, dur, limit ->
                viewModel.updateUserProfile(level, exp, pGoal, sGoal, equip, days, dur, limit)
                showEditProfileDialog = false
            }
        )
    }

    if (editingPR != null) {
        EditPRDialog(
            record = editingPR!!,
            onDismiss = { editingPR = null },
            onSave = { newVal ->
                val pr = editingPR!!
                viewModel.savePR(pr.recordKey, pr.exerciseName, newVal, pr.unit, pr.category)
                editingPR = null
            }
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset All Training Memory?") },
            text = { Text("This will reset your local profile, workouts, and PR history back to starting defaults.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetAllData()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Reset All Data", color = MaterialTheme.colorScheme.onError)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("Cancel") }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ATHLETE PROFILE",
                        style = MaterialTheme.typography.labelMedium,
                        color = AthleticCyan,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Training Memory",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                OutlinedButton(
                    onClick = { showEditProfileDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AthleticCyan),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AthleticCyan.copy(alpha = 0.5f)),
                    modifier = Modifier.testTag("edit_profile_button")
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit")
                }
            }
        }

        // Section Tabs
        item {
            TabRow(
                selectedTabIndex = selectedSection,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = AthleticCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedSection]),
                        color = AthleticCyan
                    )
                }
            ) {
                sectionTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedSection == index,
                        onClick = { selectedSection = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedSection == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedSection == index) AthleticCyan else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }
        }

        when (selectedSection) {
            0 -> {
                // Section 0: Rank & Tier Gamification
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AthleticCyan.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "CURRENT ATHLETE TIER",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AthleticCyan,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.5.sp
                                    )
                                    Text(
                                        text = "${rankData.currentTier.tierName.uppercase()} ${rankData.currentTier.division}",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = rankData.nextTier?.let { "Target: ${it.tierName} ${it.division}" } ?: "Maximum Tier Achieved",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = AthleticLime.copy(alpha = 0.15f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, AthleticLime.copy(alpha = 0.3f))
                                ) {
                                    Text(
                                        text = "#${rankData.globalLeaderboardRank} GLOBAL",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp,
                                        color = AthleticLime,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            LinearProgressIndicator(
                                progress = { rankData.progressRatio },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = AthleticCyan,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${rankData.currentXp} XP Total",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = AthleticCyan
                                )
                                Text(
                                    text = rankData.nextTier?.let { "${it.minXp} XP Required" } ?: "Apex Master",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "XP Earned By Category",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Workouts", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("+${rankData.workoutsXp} XP", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AthleticCyan)
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Skills Mastered", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("+${rankData.skillsXp} XP", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AthleticLime)
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("PR Milestones", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("+${rankData.prsXp} XP", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AthleticOrange)
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Kinetic Streak", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("+${rankData.streakXp} XP", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFFFF3366))
                            }
                        }
                    }
                }
            }
            1 -> {
                // Section 1: Profile & Identity
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AthleticCyan.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(AthleticCyan.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = AthleticCyan, modifier = Modifier.size(26.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Alex Vance", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Text("@alex_titan • crmyhsk@gmail.com", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = AthleticLime.copy(alpha = 0.15f)
                                ) {
                                    Text("GOOGLE SYNCED", fontSize = 9.sp, fontWeight = FontWeight.Black, color = AthleticLime, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                                }
                            }

                            Button(
                                onClick = { showOnboardingWizard = true },
                                colors = ButtonDefaults.buttonColors(containerColor = AthleticCyan, contentColor = Color.Black),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Re-Run 5-Step Calibration Wizard", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                item {
                    val profile = userProfile ?: UserProfileEntity()
                    TrainingMemoryCard(profile = profile, streak = streak)
                }

                item {
                    val profile = userProfile ?: UserProfileEntity()
                    EquipmentAndPreferencesCard(
                        profile = profile,
                        restDuration = settings.restTimeSeconds,
                        onRestChange = { viewModel.setRestDuration(it) },
                        onResetClick = { showResetDialog = true }
                    )
                }
            }
            2 -> {
                // Section 2: PR Records
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Personal Records (${prs.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Tap to edit • Bin to delete",
                            style = MaterialTheme.typography.bodySmall,
                            color = AthleticCyan
                        )
                    }
                }

                items(prs) { pr ->
                    PersonalRecordItem(
                        record = pr,
                        onClick = { editingPR = pr },
                        onDelete = { viewModel.deletePR(pr.recordKey) }
                    )
                }
            }
            3 -> {
                // Section 3: Progression Insights
                item {
                    Text(
                        text = "Rules-Based Performance Insights",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Calculated directly from your workout completions and rep thresholds.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                items(insights) { insight ->
                    ProgressionInsightCard(
                        insight = insight,
                        onAction = {
                            viewModel.selectTab("coach")
                            viewModel.sendCoachPrompt("Help me implement this progression: ${insight.title}")
                        }
                    )
                }
            }
            4 -> {
                // Section 4: Study Me Analysis
                item {
                    StudyMeCard(
                        report = studyReport,
                        onConsultCoach = {
                            viewModel.selectTab("coach")
                            viewModel.sendCoachPrompt("Study my profile and PR performance to propose my next training cycle")
                        }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun TrainingMemoryCard(
    profile: UserProfileEntity,
    streak: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(AthleticCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = AthleticCyan,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "${profile.fitnessLevel} Athlete",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Experience: ${profile.experienceYears}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(AthleticOrange.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = AthleticOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$streak Day Streak",
                            style = MaterialTheme.typography.labelSmall,
                            color = AthleticOrange,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Current Program Phase
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "CURRENT TRAINING PHASE",
                        style = MaterialTheme.typography.labelSmall,
                        color = AthleticCyan,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = profile.currentProgramPhase,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Week ${profile.currentWeek} of ${profile.totalWeeksInPhase}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Goals & Schedule
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileMetricItem(
                    label = "PRIMARY GOAL",
                    value = profile.primaryGoal,
                    modifier = Modifier.weight(1f)
                )
                ProfileMetricItem(
                    label = "SCHEDULE",
                    value = "${profile.trainingDaysPerWeek} Days / ${profile.workoutDurationMinutes} Min",
                    modifier = Modifier.weight(1f)
                )
            }

            if (profile.jointLimitations.isNotBlank() && profile.jointLimitations != "None") {
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Warning,
                        contentDescription = null,
                        tint = AthleticOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Limitations: ${profile.jointLimitations}",
                        style = MaterialTheme.typography.bodySmall,
                        color = AthleticOrange
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileMetricItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun EquipmentAndPreferencesCard(
    profile: UserProfileEntity,
    restDuration: Int,
    onRestChange: (Int) -> Unit,
    onResetClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Available Equipment",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = profile.equipmentAvailable,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Rest Timer Duration",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(45, 60, 90, 120).forEach { seconds ->
                    val isSelected = restDuration == seconds
                    Surface(
                        onClick = { onRestChange(seconds) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) AthleticCyan else MaterialTheme.colorScheme.surfaceVariant,
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ) {
                        Text(
                            text = "${seconds}s",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 10.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = onResetClick,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Reset Training History & Memory")
            }
        }
    }
}

@Composable
fun StudyMeCard(
    report: StudyMeReport,
    onConsultCoach: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AthleticCyan.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .testTag("study_me_report_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = AthleticCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "STUDY ME DIAGNOSTIC",
                            style = MaterialTheme.typography.labelSmall,
                            color = AthleticCyan,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Training Performance Audit",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(AthleticCyan.copy(alpha = 0.15f))
                        .border(2.dp, AthleticCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${report.adherenceScore}%",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Black,
                            color = AthleticCyan
                        )
                        Text(
                            text = "Score",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 8.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Strength & Skill Assessment
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Strength Capacity: ${report.strengthAssessment}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Skill Readiness: ${report.skillReadiness}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Strengths
            Text(
                text = "Observed Strengths",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = AthleticLime
            )
            Spacer(modifier = Modifier.height(6.dp))
            report.keyStrengths.forEach { strength ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = AthleticLime, modifier = Modifier.size(14.dp).padding(top = 2.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = strength, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Areas to improve
            Text(
                text = "Areas for Progressive Overload",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = AthleticOrange
            )
            Spacer(modifier = Modifier.height(6.dp))
            report.areasToImprove.forEach { area ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(imageVector = Icons.Outlined.Warning, contentDescription = null, tint = AthleticOrange, modifier = Modifier.size(14.dp).padding(top = 2.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = area, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Recommendation
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AthleticCyan.copy(alpha = 0.1f),
                border = androidx.compose.foundation.BorderStroke(1.dp, AthleticCyan.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "RECOMMENDED NEXT PHASE",
                        style = MaterialTheme.typography.labelSmall,
                        color = AthleticCyan,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = report.recommendedNextPhase,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onConsultCoach,
                colors = ButtonDefaults.buttonColors(containerColor = AthleticCyan, contentColor = Color.Black),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate Next Phase with AI Coach", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ProgressionInsightCard(
    insight: ProgressionInsight,
    onAction: () -> Unit
) {
    val accentColor = when (insight.type) {
        InsightType.LEVEL_UP -> AthleticLime
        InsightType.DELOAD -> AthleticOrange
        InsightType.MILESTONE -> AthleticCyan
        InsightType.RECOVERY -> Color(0xFF81D4FA)
        InsightType.TECHNIQUE -> Color(0xFFCE93D8)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (insight.type) {
                                InsightType.LEVEL_UP -> Icons.AutoMirrored.Filled.TrendingUp
                                InsightType.DELOAD -> Icons.Outlined.Warning
                                InsightType.MILESTONE -> Icons.Default.Star
                                else -> Icons.Default.Info
                            },
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = insight.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = insight.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            if (insight.actionText != null) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onAction,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = accentColor),
                    border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.5f))
                ) {
                    Text(text = insight.actionText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PersonalRecordItem(
    record: PersonalRecord,
    onClick: () -> Unit,
    onDelete: () -> Unit = {}
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("pr_item_${record.recordKey}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.exerciseName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Logged: ${record.dateAchieved}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${record.recordValue}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = AthleticCyan
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = record.unit,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onClick,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit PR",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete PR",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EditProfileDialog(
    profile: UserProfileEntity,
    onDismiss: () -> Unit,
    onSave: (level: String, exp: String, pGoal: String, sGoal: String, equip: String, days: Int, dur: Int, limit: String) -> Unit
) {
    var level by remember { mutableStateOf(profile.fitnessLevel) }
    var exp by remember { mutableStateOf(profile.experienceYears) }
    var primaryGoal by remember { mutableStateOf(profile.primaryGoal) }
    var secondaryGoal by remember { mutableStateOf(profile.secondaryGoal) }
    var equipment by remember { mutableStateOf(profile.equipmentAvailable) }
    var daysText by remember { mutableStateOf(profile.trainingDaysPerWeek.toString()) }
    var durationText by remember { mutableStateOf(profile.workoutDurationMinutes.toString()) }
    var limitations by remember { mutableStateOf(profile.jointLimitations) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Training Profile",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = level,
                    onValueChange = { level = it },
                    label = { Text("Fitness Level (Beginner / Intermediate / Advanced)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = exp,
                    onValueChange = { exp = it },
                    label = { Text("Experience (e.g. 1-2 years)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = primaryGoal,
                    onValueChange = { primaryGoal = it },
                    label = { Text("Primary Goal") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = equipment,
                    onValueChange = { equipment = it },
                    label = { Text("Available Equipment") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = daysText,
                        onValueChange = { daysText = it },
                        label = { Text("Days/Wk") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = durationText,
                        onValueChange = { durationText = it },
                        label = { Text("Duration (min)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = limitations,
                    onValueChange = { limitations = it },
                    label = { Text("Joint Limitations / Notes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val days = daysText.toIntOrNull() ?: profile.trainingDaysPerWeek
                    val dur = durationText.toIntOrNull() ?: profile.workoutDurationMinutes
                    onSave(level, exp, primaryGoal, secondaryGoal, equipment, days, dur, limitations)
                },
                colors = ButtonDefaults.buttonColors(containerColor = AthleticCyan, contentColor = Color.Black)
            ) {
                Text("Save Changes", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EditPRDialog(
    record: PersonalRecord,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    var valText by remember { mutableStateOf(record.recordValue.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Update ${record.exerciseName} PR",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Enter your new personal record in ${record.unit}:")
                OutlinedTextField(
                    value = valText,
                    onValueChange = { valText = it },
                    label = { Text("Record (${record.unit})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newVal = valText.toIntOrNull() ?: record.recordValue
                    onSave(newVal)
                },
                colors = ButtonDefaults.buttonColors(containerColor = AthleticCyan, contentColor = Color.Black)
            ) {
                Text("Update PR", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
