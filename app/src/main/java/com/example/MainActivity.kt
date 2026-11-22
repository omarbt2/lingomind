package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.ui.semaViewModel
import com.example.ui.screens.*
import com.example.ui.theme.semaTheme
import com.example.ui.navigation.MainDestination

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            semaTheme {
                val viewModel: semaViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

                var currentScreen by remember { mutableStateOf<MainDestination>(MainDestination.Tabs) }
                var backStack by remember { mutableStateOf<MutableList<MainDestination>>(mutableListOf()) }

                val onNavigate = { destination: MainDestination ->
                    backStack.add(currentScreen)
                    currentScreen = destination
                }

                val onBack = {
                    if (backStack.isNotEmpty()) {
                        currentScreen = backStack.removeAt(backStack.lastIndex)
                    } else {
                        currentScreen = MainDestination.Tabs
                    }
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            if (targetState is MainDestination.Lesson
                                || targetState is MainDestination.MilestoneTest) {
                                slideInHorizontally(tween(300)) { it } + fadeIn() togetherWith
                                    slideOutHorizontally(tween(300)) { -it } + fadeOut()
                            } else if (targetState is MainDestination.Friends
                                || targetState is MainDestination.AchievementsList
                                || targetState is MainDestination.EditProfile
                                || targetState is MainDestination.FlashcardDeckOverview
                                || targetState is MainDestination.FlashcardReview
                                || targetState is MainDestination.StreakCalendar
                                || targetState is MainDestination.Support
                                || targetState is MainDestination.Settings
                                || targetState is MainDestination.Bookmarks
                                || targetState is MainDestination.GemHistory
                                || targetState is MainDestination.Recap
                                || targetState is MainDestination.Pronunciation
                                || targetState is MainDestination.Listening
                                || targetState is MainDestination.AIChat
                                || targetState is MainDestination.Writing
                                || targetState is MainDestination.NotificationSettings
                                || targetState is MainDestination.DailyChallenges
                                || targetState is MainDestination.MilestoneResult
                                || targetState is MainDestination.About) {
                                slideInHorizontally(tween(300)) { it } + fadeIn() togetherWith
                                    slideOutHorizontally(tween(300)) { -it } + fadeOut()
                            } else {
                                slideInHorizontally(tween(300)) { -it } + fadeIn() togetherWith
                                    slideOutHorizontally(tween(300)) { it } + fadeOut()
                            }
                        },
                        label = "screen_transition"
                    ) { screen ->
                        when (screen) {
                            is MainDestination.Tabs -> {
                                MainTabScreen(
                                    viewModel = viewModel,
                                    onStartLesson = { lessonId ->
                                        viewModel.startLesson(lessonId)
                                        onNavigate(MainDestination.Lesson(lessonId))
                                    },
                                    onNavigate = onNavigate
                                )
                            }
                            is MainDestination.Lesson -> {
                                LessonScreen(
                                    viewModel = viewModel,
                                    onBackToHome = { onBack() },
                                    onNavigate = onNavigate
                                )
                            }
                            is MainDestination.MilestoneTest -> {
                                MilestoneTestScreenWrapper(
                                    viewModel = viewModel,
                                    phaseId = screen.phaseId,
                                    onBack = { onBack() },
                                    onNavigateToResult = { phaseId, score, passed ->
                                        viewModel.resetMilestoneTest()
                                        onNavigate(MainDestination.MilestoneResult(phaseId, score, passed))
                                    }
                                )
                            }
                            is MainDestination.Friends -> {
                                FriendsScreenWrapper(
                                    viewModel = viewModel,
                                    onBack = { onBack() }
                                )
                            }
                            is MainDestination.AchievementsList -> {
                                AchievementsListScreenWrapper(
                                    viewModel = viewModel,
                                    onBack = { onBack() }
                                )
                            }
                            is MainDestination.EditProfile -> {
                                EditProfileScreenWrapper(
                                    viewModel = viewModel,
                                    onBack = { onBack() }
                                )
                            }
                            is MainDestination.FlashcardDeckOverview -> {
                                FlashcardDeckOverviewScreenWrapper(
                                    viewModel = viewModel,
                                    onBack = { onBack() }
                                )
                            }
                            is MainDestination.FlashcardReview -> {
                                FlashcardReviewScreenWrapper(
                                    viewModel = viewModel,
                                    deckId = screen.deckId,
                                    onBack = { onBack() }
                                )
                            }
                            is MainDestination.StreakCalendar -> {
                                StreakCalendarScreenWrapper(
                                    viewModel = viewModel,
                                    onBack = { onBack() }
                                )
                            }
                            is MainDestination.Support -> {
                                SupportScreenWrapper(
                                    onBack = { onBack() }
                                )
                            }
                            is MainDestination.Settings -> {
                                SettingsScreen(
                                    viewModel = viewModel,
                                    onBack = { onBack() }
                                )
                            }
                            is MainDestination.Bookmarks -> {
                                BookmarksScreen(
                                    viewModel = viewModel,
                                    onBack = { onBack() }
                                )
                            }
                            is MainDestination.GemHistory -> {
                                GemHistoryScreen(
                                    viewModel = viewModel,
                                    onBack = { onBack() }
                                )
                            }
                            is MainDestination.Recap -> {
                                RecapScreen(
                                    viewModel = viewModel,
                                    onBack = { onBack() }
                                )
                            }
                            is MainDestination.Pronunciation -> {
                                PronunciationScreenWrapper(
                                    viewModel = viewModel,
                                    lessonId = 1,
                                    onBack = { onBack() }
                                )
                            }
                            is MainDestination.Listening -> {
                                ListeningExerciseScreenWrapper(
                                    viewModel = viewModel,
                                    lessonId = screen.lessonId,
                                    onBack = { onBack() }
                                )
                            }
                            is MainDestination.AIChat -> {
                                AIChatScreenWrapper(
                                    viewModel = viewModel,
                                    onBack = { onBack() }
                                )
                            }
                            is MainDestination.Writing -> {
                                WritingPracticeScreenWrapper(
                                    viewModel = viewModel,
                                    onBack = { onBack() }
                                )
                            }
                            is MainDestination.NotificationSettings -> {
                                NotificationSettingsScreenWrapper(
                                    viewModel = viewModel,
                                    onBack = { onBack() }
                                )
                            }
                            is MainDestination.DailyChallenges -> {
                                DailyChallengesScreenWrapper(
                                    viewModel = viewModel,
                                    onBack = { onBack() }
                                )
                            }
                            is MainDestination.MilestoneResult -> {
                                MilestoneResultScreenWrapper(
                                    viewModel = viewModel,
                                    phaseId = screen.phaseId,
                                    score = screen.score,
                                    passed = screen.passed,
                                    onBack = { onBack() }
                                )
                            }
                            is MainDestination.About -> {
                                AboutScreenWrapper(
                                    onBack = { onBack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
