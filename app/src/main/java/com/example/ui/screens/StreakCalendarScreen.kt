package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.ui.components.semaCard
import com.example.ui.components.semaIconButton
import com.example.ui.components.PulseAnimation
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreakCalendarScreen(
    currentStreak: Int,
    activeDays: Set<LocalDate>,
    onBack: () -> Unit
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "تقويم الحماسة",
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
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Streak badge
                var badgeVisible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) { delay(100); badgeVisible = true }
                AnimatedVisibility(visible = badgeVisible, enter = fadeIn() + scaleIn()) {
                semaCard(highlighted = true) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PulseAnimation {
                            Icon(
                                imageVector = semaIcons.localFireDepartment(),
                                contentDescription = null,
                                tint = XpYellow,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "السلسلة الحالية",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                            Text(
                                text = "$currentStreak أيام متتالية",
                                style = MaterialTheme.typography.headlineMedium,
                                color = XpYellow
                            )
                        }
                    }
                }
                }

                // Month navigation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        currentMonth = currentMonth.minusMonths(1)
                    }) {
                        Icon(
                            imageVector = semaIcons.chevronRight(),
                            contentDescription = "الشهر السابق",
                            tint = TextPrimary
                        )
                    }

                    Text(
                        text = getMonthDisplayName(currentMonth),
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )

                    IconButton(onClick = {
                        currentMonth = currentMonth.plusMonths(1)
                    }) {
                        Icon(
                            imageVector = semaIcons.chevronLeft(),
                            contentDescription = "الشهر التالي",
                            tint = TextPrimary
                        )
                    }
                }

                // Day of week headers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val dayNames = listOf("أحد", "إثن", "ثلا", "أرب", "خمي", "جمع", "سبت")
                    dayNames.forEach { name ->
                        Text(
                            text = name,
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Calendar grid
                semaCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val daysInMonth = currentMonth.lengthOfMonth()
                        val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value % 7
                        val today = LocalDate.now()

                        val weeks = (firstDayOfWeek + daysInMonth + 6) / 7
                        var dayCounter = 1

                        repeat(weeks) { week ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                repeat(7) { dayOfWeek ->
                                    if (week == 0 && dayOfWeek < firstDayOfWeek || dayCounter > daysInMonth) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    } else {
                                        val day = dayCounter
                                        val date = currentMonth.atDay(day)
                                        val isActive = activeDays.contains(date)
                                        val isToday = date == today
                                        val isPast = date.isBefore(today)
                                        val cellIndex = dayCounter - 1

                                        DayCell(
                                            day = day,
                                            isActive = isActive,
                                            isToday = isToday,
                                            isPast = isPast,
                                            staggerDelayMs = cellIndex * 20L,
                                            modifier = Modifier.weight(1f)
                                        )
                                        dayCounter++
                                    }
                                }
                            }
                        }
                    }
                }

                // Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally)
                ) {
                    LegendItem(color = SuccessGreen, label = "يوم نشط")
                    LegendItem(color = SurfaceVariant, label = "بدون نشاط")
                    LegendItem(color = PrimaryBlue, label = "اليوم")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun DayCell(
    day: Int,
    isActive: Boolean,
    isToday: Boolean,
    isPast: Boolean,
    modifier: Modifier = Modifier,
    staggerDelayMs: Long = 0L
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(staggerDelayMs)
        visible = true
    }

    val bgColor = when {
        isActive -> SuccessGreen
        isToday -> PrimaryBlue.copy(alpha = 0.2f)
        else -> Color.Transparent
    }

    val textColor = when {
        isActive -> TextPrimary
        isToday -> PrimaryBlue
        isPast -> TextSecondary.copy(alpha = 0.5f)
        else -> TextSecondary
    }

    val borderColor = if (isToday && !isActive) PrimaryBlue else Color.Transparent

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300)) + scaleIn(animationSpec = spring(dampingRatio = 0.6f))
    ) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(bgColor)
            .border(1.dp, borderColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (isToday && !isActive) {
            PulseAnimation {
                Text(
                    text = "$day",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        } else {
            Text(
                text = "$day",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isActive || isToday) FontWeight.Bold else FontWeight.Normal,
                color = textColor
            )
        }
    }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

private fun getMonthDisplayName(month: YearMonth): String {
    val arabicMonths = mapOf(
        1 to "يناير", 2 to "فبراير", 3 to "مارس", 4 to "أبريل",
        5 to "مايو", 6 to "يونيو", 7 to "يوليو", 8 to "أغسطس",
        9 to "سبتمبر", 10 to "أكتوبر", 11 to "نوفمبر", 12 to "ديسمبر"
    )
    return "${arabicMonths[month.monthValue]} ${month.year}"
}
