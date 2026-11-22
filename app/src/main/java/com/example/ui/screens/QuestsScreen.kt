package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.data.DailyChallengeEntity
import com.example.data.UserDailyChallengeEntity
import com.example.ui.semaViewModel
import com.example.ui.components.LoadingSkeleton
import com.example.ui.components.semaButton
import com.example.ui.components.semaCard
import com.example.ui.components.semaIconButton
import com.example.ui.components.semaProgressBar
import com.example.ui.components.semaAvatar
import com.example.ui.components.StaggeredEntrance
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestsScreen(
    viewModel: semaViewModel,
    todaysChallenge: DailyChallengeEntity?,
    userDailyChallenge: UserDailyChallengeEntity?,
    onAuthClick: () -> Unit,
    onBack: () -> Unit,
    isLoading: Boolean = false
) {

    if (isLoading) {
        LoadingSkeleton()
    } else {
        val isCompleted = userDailyChallenge?.completed == true
        val progress = if (isCompleted) 1f else 0f

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = "المهام اليومية",
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // League Lock Card
                    StaggeredEntrance(index = 0) {
                    semaCard {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            semaAvatar(
                                size = 56.dp,
                                backgroundColor = XpYellow.copy(alpha = 0.15f),
                                content = {
                                    Icon(
                                        imageVector = semaIcons.emojiEvents(),
                                        contentDescription = "Trophy",
                                        tint = XpYellow,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            )

                            Column {
                                Text(
                                    text = "دوري اللغات مفتوح!",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = XpYellow
                                )
                                Text(
                                    text = "أكمل الدروس اليومية وتنافس مع زملائك.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                    }

                    // Daily Quest Progress
                    todaysChallenge?.let { challenge ->
                        StaggeredEntrance(index = 1) {
                        semaCard {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = challenge.titleAr,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = if (isCompleted) "مكتملة" else "قيد التنفيذ",
                                        color = if (isCompleted) SuccessGreen else XpYellow,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    semaAvatar(
                                        size = 48.dp,
                                        backgroundColor = XpYellow.copy(alpha = 0.15f),
                                        content = {
                                            Icon(
                                                imageVector = semaIcons.flashOn(),
                                                contentDescription = "Energy",
                                                tint = XpYellow,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    )

                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = challenge.descriptionAr,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = TextPrimary,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        semaProgressBar(
                                            progress = progress,
                                            color = XpYellow,
                                            label = if (isCompleted) "${challenge.xpReward} / ${challenge.xpReward}" else "0 / ${challenge.xpReward}",
                                            modifier = Modifier.height(18.dp)
                                        )
                                    }
                                }
                            }

                            if (!isCompleted) {
                                semaButton(
                                    onClick = {
                                        viewModel.completeDailyChallenge(10)
                                    },
                                    enabled = !isCompleted,
                                    backgroundColor = PrimaryBlue
                                ) {
                                    Text(
                                        text = "إكمال المهمة",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }
                        }
                    }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingSkeleton() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LoadingSkeleton(modifier = Modifier.width(200.dp).height(40.dp))
            LoadingSkeleton(modifier = Modifier.width(280.dp).height(80.dp).padding(top = 8.dp))
            LoadingSkeleton(modifier = Modifier.width(300.dp).height(120.dp).padding(top = 8.dp))
        }
    }
}
