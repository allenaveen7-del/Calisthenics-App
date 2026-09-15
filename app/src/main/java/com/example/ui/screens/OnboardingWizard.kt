package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.AthleticCyan
import com.example.ui.theme.AthleticLime
import com.example.ui.theme.AthleticOrange

@Composable
fun OnboardingWizard(
    initialLevel: String = "Intermediate",
    initialGoal: String = "Skill Mastery & Relative Strength",
    initialEquipment: String = "Pull-up Bar, Dip Station, Floor, Rings",
    initialDays: Int = 5,
    initialWeight: Float = 74.5f,
    initialHeight: Float = 178f,
    onDismiss: () -> Unit,
    onFinish: (
        level: String,
        goal: String,
        equipment: String,
        days: Int,
        weight: Float,
        height: Float
    ) -> Unit
) {
    var step by remember { mutableIntStateOf(1) } // 1 to 5

    var selectedLevel by remember { mutableStateOf(initialLevel) }
    var selectedGoal by remember { mutableStateOf(initialGoal) }
    var selectedEquipmentList by remember {
        mutableStateOf(
            initialEquipment.split(",").map { it.trim() }.filter { it.isNotBlank() }.toMutableList()
        )
    }
    var trainingDays by remember { mutableIntStateOf(initialDays) }
    var weightInput by remember { mutableStateOf(initialWeight.toString()) }
    var heightInput by remember { mutableStateOf(initialHeight.toInt().toString()) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Top Progress & Step Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (step > 1) step-- else onDismiss()
                        }
                    ) {
                        Icon(
                            imageVector = if (step > 1) Icons.AutoMirrored.Filled.ArrowBack else Icons.Default.Close,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = "STEP $step OF 5",
                        style = MaterialTheme.typography.labelSmall,
                        color = AthleticCyan,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )

                    TextButton(onClick = onDismiss) {
                        Text("Skip", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                LinearProgressIndicator(
                    progress = { step / 5f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = AthleticCyan,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Step Content
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    AnimatedContent(
                        targetState = step,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "onboarding_step"
                    ) { currentStep ->
                        when (currentStep) {
                            1 -> FitnessLevelStep(
                                selected = selectedLevel,
                                onSelect = { selectedLevel = it }
                            )
                            2 -> PrimaryGoalStep(
                                selected = selectedGoal,
                                onSelect = { selectedGoal = it }
                            )
                            3 -> EquipmentStep(
                                selected = selectedEquipmentList,
                                onToggle = { item ->
                                    val updated = selectedEquipmentList.toMutableList()
                                    if (updated.contains(item)) updated.remove(item) else updated.add(item)
                                    selectedEquipmentList = updated
                                }
                            )
                            4 -> FrequencyStep(
                                selectedDays = trainingDays,
                                onSelectDays = { trainingDays = it }
                            )
                            5 -> PhysicalStatsStep(
                                weight = weightInput,
                                height = heightInput,
                                onWeightChange = { weightInput = it },
                                onHeightChange = { heightInput = it }
                            )
                        }
                    }
                }

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (step > 1) {
                        TextButton(onClick = { step-- }) {
                            Text("Previous", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Button(
                        onClick = {
                            if (step < 5) {
                                step++
                            } else {
                                val w = weightInput.toFloatOrNull() ?: initialWeight
                                val h = heightInput.toFloatOrNull() ?: initialHeight
                                onFinish(
                                    selectedLevel,
                                    selectedGoal,
                                    selectedEquipmentList.joinToString(", "),
                                    trainingDays,
                                    w,
                                    h
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AthleticCyan, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("onboarding_next_btn")
                    ) {
                        Text(
                            text = if (step == 5) "LOCK IN PROFILE" else "CONTINUE",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = if (step == 5) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FitnessLevelStep(
    selected: String,
    onSelect: (String) -> Unit
) {
    val levels = listOf(
        Pair("Beginner", "Building fundamental push-ups, hollow body, and deadhang capacity"),
        Pair("Intermediate", "10+ pull-ups, solid dips, working toward muscle-up and L-sit"),
        Pair("Advanced", "Strict muscle-ups, handstand holds, front lever progressions"),
        Pair("Elite / Titan", "Full Planche, One-Arm Chin-up, Iron Cross, and heavy compound iron")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Assess Current Calisthenics Level",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Kinetix calibrates your progression trees and adaptive fatigue calculations based on your baseline.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        levels.forEach { (level, desc) ->
            val isSelected = selected.equals(level, ignoreCase = true)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) AthleticCyan.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) AthleticCyan else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(level) }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .border(2.dp, if (isSelected) AthleticCyan else Color.Gray, CircleShape)
                            .background(if (isSelected) AthleticCyan else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = level,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PrimaryGoalStep(
    selected: String,
    onSelect: (String) -> Unit
) {
    val goals = listOf(
        Pair("Skill Mastery & Relative Strength", "Master Planche, Front Lever, Handstand Push-ups, and Ring Muscle-ups"),
        Pair("Hypertrophy & Kinetic Aesthetics", "Dense athletic muscle mass through high-tension calisthenics & weighted iron"),
        Pair("Tendon Resilience & Joint Longevity", "Unbreakable connective tissue, bulletproof shoulders, and scapular control"),
        Pair("Hybrid Powerlifting & Bodyweight", "Combine 200kg deadlifts and heavy squats with gymnastics ring precision")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Select Primary Training Target",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "The AI Adaptive Coach customizes volume splits and rest timers according to your target adaptation.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        goals.forEach { (goal, desc) ->
            val isSelected = selected.equals(goal, ignoreCase = true)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) AthleticLime.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) AthleticLime else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(goal) }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .border(2.dp, if (isSelected) AthleticLime else Color.Gray, CircleShape)
                            .background(if (isSelected) AthleticLime else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = goal,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EquipmentStep(
    selected: List<String>,
    onToggle: (String) -> Unit
) {
    val items = listOf(
        "Pull-up Bar",
        "Dip Station",
        "Gymnastics Rings",
        "Parallettes",
        "Floor / Bodyweight",
        "Resistance Bands",
        "Barbell & Plates",
        "Dumbbells",
        "Cable Machine",
        "Weight Vest"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Select Available Gear",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "We filter the 345+ exercise matrix to only propose movements you can perform immediately.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(4.dp))

        items.forEach { item ->
            val hasItem = selected.any { it.contains(item, ignoreCase = true) }
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (hasItem) AthleticCyan.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (hasItem) AthleticCyan else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle(item) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .border(1.5.dp, if (hasItem) AthleticCyan else Color.Gray, RoundedCornerShape(6.dp))
                            .background(if (hasItem) AthleticCyan else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        if (hasItem) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FrequencyStep(
    selectedDays: Int,
    onSelectDays: (Int) -> Unit
) {
    val options = listOf(
        Pair(3, "3 Days/Week • Full Body Recomposition"),
        Pair(4, "4 Days/Week • Upper / Lower Split"),
        Pair(5, "5 Days/Week • Push / Pull / Legs / Skill Split (Recommended)"),
        Pair(6, "6 Days/Week • Elite High-Frequency Specialization")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Set Weekly Training Frequency",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Consistent progressive overload with adequate rest days is the cornerstone of calisthenics longevity.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        options.forEach { (days, label) ->
            val isSelected = selectedDays == days
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) AthleticOrange.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) AthleticOrange else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectDays(days) }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) AthleticOrange else Color.Gray.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$days",
                            fontWeight = FontWeight.Black,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun PhysicalStatsStep(
    weight: String,
    height: String,
    onWeightChange: (String) -> Unit,
    onHeightChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Physical Metrics & Identity",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Bodyweight directly impacts relative strength ratios (lever arms for Planche, Front Lever, and OAC).",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "ATHLETE IDENTITY",
                    style = MaterialTheme.typography.labelSmall,
                    color = AthleticCyan,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Alex Vance (@alex_titan)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "crmyhsk@gmail.com • Google Sign-In Connected",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        OutlinedTextField(
            value = weight,
            onValueChange = onWeightChange,
            label = { Text("Bodyweight (kg)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = height,
            onValueChange = onHeightChange,
            label = { Text("Height (cm)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
