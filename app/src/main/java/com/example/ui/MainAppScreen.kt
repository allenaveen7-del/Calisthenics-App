package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.MilitaryTech
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.outlined.Public
import com.example.ui.screens.AiCoachScreen
import com.example.ui.screens.CommunityScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ProgramScreen
import com.example.ui.screens.ProgressScreen
import com.example.ui.screens.SkillsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WorkoutScreen
import com.example.ui.viewmodel.CoachViewModel

sealed class ScreenTab(
    val id: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : ScreenTab(
        id = "home",
        title = "Home",
        selectedIcon = Icons.Default.FitnessCenter,
        unselectedIcon = Icons.Outlined.FitnessCenter
    )

    object Program : ScreenTab(
        id = "program",
        title = "Program",
        selectedIcon = Icons.Default.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth
    )

    object Coach : ScreenTab(
        id = "coach",
        title = "AI Coach",
        selectedIcon = Icons.Default.AutoAwesome,
        unselectedIcon = Icons.Outlined.AutoAwesome
    )

    object Skills : ScreenTab(
        id = "skills",
        title = "Skills",
        selectedIcon = Icons.Default.MilitaryTech,
        unselectedIcon = Icons.Outlined.MilitaryTech
    )

    object Community : ScreenTab(
        id = "community",
        title = "Arena",
        selectedIcon = Icons.Default.Public,
        unselectedIcon = Icons.Outlined.Public
    )

    object Profile : ScreenTab(
        id = "profile",
        title = "Profile",
        selectedIcon = Icons.Default.Person,
        unselectedIcon = Icons.Outlined.Person
    )
}

@Composable
fun MainAppScreen(
    viewModel: CoachViewModel,
    modifier: Modifier = Modifier
) {
    val activeWorkout by viewModel.activeWorkout.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()

    val tabs = listOf(
        ScreenTab.Home,
        ScreenTab.Program,
        ScreenTab.Coach,
        ScreenTab.Skills,
        ScreenTab.Community,
        ScreenTab.Profile
    )

    if (activeWorkout.isActive) {
        // Dedicated Full-Screen Workout Mode
        WorkoutScreen(
            viewModel = viewModel,
            onExitWorkout = { viewModel.exitWorkout() },
            modifier = modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        )
    } else {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .testTag("main_bottom_nav")
                        .windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    tabs.forEach { tab ->
                        val isSelected = currentTab == tab.id
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.selectTab(tab.id) },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.title
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("nav_tab_${tab.id}")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Crossfade(targetState = currentTab, label = "tab_transition") { tabId ->
                    when (tabId) {
                        "home" -> HomeScreen(
                            viewModel = viewModel,
                            onStartWorkout = { program -> viewModel.startWorkout(program) },
                            onNavigateToProgram = { viewModel.selectTab("program") }
                        )
                        "program" -> ProgramScreen(
                            viewModel = viewModel,
                            onStartWorkout = { program -> viewModel.startWorkout(program) }
                        )
                        "coach" -> AiCoachScreen(viewModel = viewModel)
                        "skills" -> SkillsScreen(viewModel = viewModel)
                        "community" -> CommunityScreen(viewModel = viewModel)
                        "profile" -> ProfileScreen(viewModel = viewModel)
                        else -> HomeScreen(
                            viewModel = viewModel,
                            onStartWorkout = { program -> viewModel.startWorkout(program) },
                            onNavigateToProgram = { viewModel.selectTab("program") }
                        )
                    }
                }
            }
        }
    }
}
