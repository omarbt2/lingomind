package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.ui.LingoMindViewModel
import com.example.ui.screens.LessonScreen
import com.example.ui.screens.MainTabScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Initialize VM
                val viewModel: LingoMindViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                
                // Orchestrate Navigation State
                var currentScreen by remember { mutableStateOf("tabs") }

                Surface(modifier = Modifier.fillMaxSize()) {
                    when (currentScreen) {
                        "tabs" -> {
                            MainTabScreen(
                                viewModel = viewModel,
                                onStartLesson = { lessonId ->
                                    viewModel.startLesson(lessonId)
                                    currentScreen = "lesson"
                                }
                            )
                        }
                        "lesson" -> {
                            LessonScreen(
                                viewModel = viewModel,
                                onBackToHome = {
                                    currentScreen = "tabs"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
