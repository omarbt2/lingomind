package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.ui.semaViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecapScreen(
    viewModel: semaViewModel,
    onBack: () -> Unit
) {
    val userStreaks by viewModel.userStreaks.collectAsState()
    val userLevels by viewModel.userLevels.collectAsState()
    val xpEvents by viewModel.xpEvents.collectAsState()
    val gemWallet by viewModel.gemWallet.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchXPEvents()
        viewModel.fetchAllUserState()
    }

    val now = java.time.LocalDateTime.now()
    val weekStart = now.minusDays(now.dayOfWeek.value.toLong() - 1)
    val monthStart = now.withDayOfMonth(1)

    val weekXp = remember(xpEvents) {
        xpEvents.filter { event ->
            try {
                val date = java.time.LocalDate.ofEpochDay(event.createdAt / 86400000)
                date >= weekStart.toLocalDate() && date <= now.toLocalDate()
            } catch (e: Exception) { false }
        }.sumOf { it.xpAmount }
    }

    val monthXp = remember(xpEvents) {
        xpEvents.filter { event ->
            try {
                val date = java.time.LocalDate.ofEpochDay(event.createdAt / 86400000)
                date >= monthStart.toLocalDate() && date <= now.toLocalDate()
            } catch (e: Exception) { false }
        }.sumOf { it.xpAmount }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "ملخص الأسبوع",
                            style = MaterialTheme.typography.headlineSmall,
                            color = TextPrimary
                        )
                    },
                    navigationIcon = {
                        semaIconButton(onClick = onBack) {
                            Icon(
                                imageVector = semaIcons.arrowBack(),
                                contentDescription = "رجوع",
                                tint = TextPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
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

                // Weekly overview card
                semaCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "هذا الأسبوع",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            RecapStat(
                                value = "${weekXp}",
                                label = "نقاط خبرة",
                                color = PrimaryBlue
                            )
                            RecapStat(
                                value = "${userStreaks?.current_streak ?: 0}",
                                label = "السلسلة (أيام)",
                                color = XpYellow
                            )
                            RecapStat(
                                value = "${gemWallet?.balance ?: 0}",
                                label = "الجواهر",
                                color = PrimaryBlue
                            )
                        }
                    }
                }

                // Monthly overview
                semaCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "هذا الشهر",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            RecapStat(
                                value = "${monthXp}",
                                label = "نقاط خبرة",
                                color = PrimaryBlue
                            )
                            RecapStat(
                                value = "${userStreaks?.longest_streak ?: 0}",
                                label = "أطول سلسلة",
                                color = XpYellow
                            )
                            RecapStat(
                                value = "${userLevels?.current_level ?: 1}",
                                label = "المستوى الحالي",
                                color = SuccessGreen
                            )
                        }
                    }
                }

                // XP progress
                semaCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "التقدم",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary
                        )

                        val currentXp = userLevels?.current_xp ?: 0
                        val xpToNext = userLevels?.xp_to_next ?: 100
                        val progress = if (xpToNext > 0) {
                            (currentXp.toFloat() / xpToNext).coerceIn(0f, 1f)
                        } else 0f

                        semaProgressBar(
                            progress = progress,
                            label = "$currentXp / $xpToNext XP",
                            color = PrimaryBlue,
                            modifier = Modifier.height(20.dp)
                        )

                        Text(
                            text = "المستوى ${userLevels?.current_level ?: 1}",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Total stats card
                semaCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "الإحصائيات الكلية",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary
                        )

                        TotalStatRow(
                            label = "إجمالي الدروس المكتملة",
                            value = "${userStreaks?.total_lessons ?: 0}"
                        )
                        TotalStatRow(
                            label = "إجمالي البطاقات",
                            value = "${userStreaks?.total_flashcards ?: 0}"
                        )
                        TotalStatRow(
                            label = "إجمالي نقاط الخبرة",
                            value = "${userStreaks?.total_xp ?: 0}"
                        )
                        TotalStatRow(
                            label = "أطول سلسلة",
                            value = "${userStreaks?.longest_streak ?: 0} يوم"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun RecapStat(
    value: String,
    label: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TotalStatRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}
