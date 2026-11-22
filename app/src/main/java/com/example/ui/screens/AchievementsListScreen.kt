package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay

data class AchievementType(
    val id: Int,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val target: Int,
    val current: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsListScreen(
    achievements: List<AchievementType>,
    onBack: () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "الإنجازات",
                            style = MaterialTheme.typography.headlineSmall,
                            color = TextPrimary
                        )
                    },
                    navigationIcon = {
                        semaIconButton(
                            onClick = onBack,
                            content = {
                                Icon(
                                    imageVector = semaIcons.arrowBack(),
                                    contentDescription = "رجوع",
                                    tint = TextPrimary
                                )
                            }
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = BackgroundDark
                    )
                )
            },
            containerColor = BackgroundDark
        ) { innerPadding ->
            if (achievements.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        icon = semaIcons.emojiEvents(),
                        title = "لا توجد إنجازات بعد",
                        message = "ابدأ التعلم لفتح الإنجازات!"
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    itemsIndexed(achievements) { index, achievement ->
                        StaggeredEntrance(index = index) {
                            AchievementGridCard(achievement = achievement)
        }
    }

    }
    }
    }
    }
}

@Composable
private fun AchievementGridCard(
    achievement: AchievementType
) {
    val isUnlocked = achievement.current >= achievement.target
    val progress = if (achievement.target > 0) {
        achievement.current.toFloat() / achievement.target
    } else 0f

    var showCelebration by remember { mutableStateOf(false) }
    LaunchedEffect(achievement.current) {
        if (isUnlocked) {
            delay(400)
            showCelebration = true
        }
    }

    semaCard(
        modifier = Modifier.fillMaxWidth(),
        highlighted = isUnlocked
    ) {
        if (showCelebration) {
            CorrectAnswerBounce {
                AchievementCardContent(achievement, isUnlocked, progress)
            }
            LaunchedEffect(Unit) {
                delay(600)
                showCelebration = false
            }
        } else {
            AchievementCardContent(achievement, isUnlocked, progress)
        }
    }
}

@Composable
private fun AchievementCardContent(
    achievement: AchievementType,
    isUnlocked: Boolean,
    progress: Float
) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Icon badge
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) achievement.color.copy(alpha = 0.15f)
                        else SurfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = achievement.icon,
                    contentDescription = achievement.name,
                    tint = if (isUnlocked) achievement.color else TextSecondary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = achievement.name,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = achievement.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (isUnlocked) {
                Text(
                    text = "✓ مُنقَذ",
                    style = MaterialTheme.typography.labelMedium,
                    color = SuccessGreen
                )
            } else {
                semaProgressBar(
                    progress = progress,
                    color = achievement.color,
                    label = "${achievement.current}/${achievement.target}"
                )
    }

    }
}
