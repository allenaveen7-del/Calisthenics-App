package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainAppScreen
import com.example.ui.theme.CalisthenicsCoachTheme
import com.example.ui.viewmodel.CoachViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val coachViewModel: CoachViewModel = viewModel()
            val settings by coachViewModel.settings.collectAsStateWithLifecycle()

            CalisthenicsCoachTheme(appThemeSetting = settings.appTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainAppScreen(viewModel = coachViewModel)
                }
            }
        }
    }
}

