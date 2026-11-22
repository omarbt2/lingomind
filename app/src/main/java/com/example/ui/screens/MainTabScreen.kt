package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.data.LeaderboardWeeklyEntity
import com.example.ui.semaViewModel
import com.example.ui.components.semaBottomNav
import com.example.ui.theme.*
import com.example.ui.navigation.MainDestination

@Composable
fun MainTabScreen(
    viewModel: semaViewModel,
    onStartLesson: (Int) -> Unit,
    onNavigate: (MainDestination) -> Unit
) {
    var activeTab by remember { mutableIntStateOf(0) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            bottomBar = {
                semaBottomNav(
                    selectedIndex = activeTab,
                    onItemSelected = { activeTab = it }
                )
            },
            containerColor = BackgroundDark
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
AnimatedContent(
                    targetState = activeTab,
                    transitionSpec = {
                        ContentTransform(
                            fadeIn(animationSpec = tween(150)),
                            fadeOut(animationSpec = tween(150))
                        )
                    }
                ) { targetTab ->
                    when (targetTab) {
                        0 -> HomeScreen(
                            viewModel = viewModel,
                            onStartLesson = onStartLesson,
                            onNavigate = onNavigate
                        )
                        1 -> LeaguesScreenWrapper(
                            viewModel = viewModel,
                            onStartLesson = { onStartLesson(1) },
                            onBack = { activeTab = 0 }
                        )
                        2 -> QuestsScreenWrapper(
                            viewModel = viewModel,
                            onAuthClick = { activeTab = 4 },
                            onBack = { activeTab = 0 }
                        )
                        3 -> ShopScreenWrapper(
                            viewModel = viewModel,
                            onBack = { activeTab = 0 }
                        )
                        4 -> ProfileScreen(
                            viewModel = viewModel,
                            onNavigate = onNavigate
                        )
                    }
                }
            }
        }
    }
}
