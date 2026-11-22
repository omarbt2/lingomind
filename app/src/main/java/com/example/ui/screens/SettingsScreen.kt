package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.data.UserSettingsEntity
import com.example.ui.semaViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: semaViewModel,
    onBack: () -> Unit
) {
    val userSettings by viewModel.userSettings.collectAsState()
    val userProgress by viewModel.userProgress.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchUserSettings()
    }

    val currentSettings = userSettings ?: UserSettingsEntity(
        user_id = "",
        daily_goal_min = 15,
        reminder_time = null,
        preferred_lang = "ar",
        notifications_on = true,
        streak_reminder_hour = 9,
        xp_goal_daily = 50,
        preferred_difficulty = "medium",
        show_leaderboard = true,
        audio_speed = 1.0f
    )

    var dailyGoal by remember(currentSettings) { mutableStateOf(currentSettings.daily_goal_min.toString()) }
    var xpGoal by remember(currentSettings) { mutableStateOf(currentSettings.xp_goal_daily.toString()) }
    var reminderHour by remember(currentSettings) { mutableIntStateOf(currentSettings.streak_reminder_hour ?: 9) }
    var notifsEnabled by remember(currentSettings) { mutableStateOf(currentSettings.notifications_on) }
    var showLeaderboard by remember(currentSettings) { mutableStateOf(currentSettings.show_leaderboard) }
    var audioSpeed by remember(currentSettings) { mutableFloatStateOf(currentSettings.audio_speed) }
    var selectedDifficulty by remember(currentSettings) { mutableStateOf(currentSettings.preferred_difficulty ?: "medium") }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "الإعدادات",
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
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Learning Goals Section
                Text(
                    text = "أهداف التعلم",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )

                semaCard {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "الهدف اليومي (بالدقائق)",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary
                        )
                        OutlinedTextField(
                            value = dailyGoal,
                            onValueChange = { dailyGoal = it.filter { c -> c.isDigit() } },
                            label = { Text("الدقائق", color = TextSecondary) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = Outline,
                                cursorColor = PrimaryBlue
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        HorizontalDivider(color = OutlineVariant)

                        Text(
                            text = "هدف XP اليومي",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary
                        )
                        OutlinedTextField(
                            value = xpGoal,
                            onValueChange = { xpGoal = it.filter { c -> c.isDigit() } },
                            label = { Text("XP", color = TextSecondary) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = Outline,
                                cursorColor = PrimaryBlue
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                // Difficulty Section
                Text(
                    text = "مستوى الصعوبة",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )

                semaCard {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DifficultyOption(
                            label = "سهل",
                            description = "تمارين أقل تحدياً، وقت أطول",
                            isSelected = selectedDifficulty == "easy",
                            onClick = { selectedDifficulty = "easy" }
                        )
                        DifficultyOption(
                            label = "متوسط",
                            description = "توازن بين التحدي والسهولة",
                            isSelected = selectedDifficulty == "medium",
                            onClick = { selectedDifficulty = "medium" }
                        )
                        DifficultyOption(
                            label = "صعب",
                            description = "تمارين متقدمة، تحدٍ أكبر",
                            isSelected = selectedDifficulty == "hard",
                            onClick = { selectedDifficulty = "hard" }
                        )
                    }
                }

                // Audio Speed Section
                Text(
                    text = "سرعة الصوت",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )

                semaCard {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${"%.1f".format(audioSpeed)}x",
                            style = MaterialTheme.typography.titleMedium,
                            color = PrimaryBlue
                        )
                        Slider(
                            value = audioSpeed,
                            onValueChange = { audioSpeed = it },
                            valueRange = 0.5f..2.0f,
                            steps = 5,
                            colors = SliderDefaults.colors(
                                thumbColor = PrimaryBlue,
                                activeTrackColor = PrimaryBlue,
                                inactiveTrackColor = SurfaceVariant
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("0.5x", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                            Text("1.0x", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                            Text("2.0x", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                // Notifications Section
                Text(
                    text = "الإشعارات",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )

                semaCard {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SettingsToggle(
                            title = "الإشعارات",
                            description = "تذكير يومي للمحافظة على السلسلة",
                            checked = notifsEnabled,
                            onCheckedChange = { notifsEnabled = it }
                        )

                        if (notifsEnabled) {
                            HorizontalDivider(color = OutlineVariant)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "وقت التذكير",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "ساعة التذكير اليومي",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    semaIconButton(
                                        onClick = { reminderHour = (reminderHour - 1).coerceIn(0, 23) }
                                    ) {
                                        Icon(
                                            imageVector = semaIcons.expandLess(),
                                            contentDescription = "تقليل",
                                            tint = PrimaryBlue,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Text(
                                        text = "${reminderHour}:00",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextPrimary
                                    )
                                    semaIconButton(
                                        onClick = { reminderHour = (reminderHour + 1).coerceIn(0, 23) }
                                    ) {
                                        Icon(
                                            imageVector = semaIcons.expandMore(),
                                            contentDescription = "زيادة",
                                            tint = PrimaryBlue,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Privacy Section
                Text(
                    text = "الخصوصية",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )

                semaCard {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SettingsToggle(
                            title = "إظهار في لوحة الصدارة",
                            description = "ظهور اسمك في ترتيب المتسابقين الأسبوعي",
                            checked = showLeaderboard,
                            onCheckedChange = { showLeaderboard = it }
                        )
                    }
                }

                // Save button
                semaButton(
                    onClick = {
                        val updated = currentSettings.copy(
                            daily_goal_min = dailyGoal.toIntOrNull() ?: 15,
                            xp_goal_daily = xpGoal.toIntOrNull() ?: 50,
                            preferred_difficulty = selectedDifficulty,
                            audio_speed = audioSpeed,
                            notifications_on = notifsEnabled,
                            show_leaderboard = showLeaderboard,
                            streak_reminder_hour = if (notifsEnabled) reminderHour else null
                        )
                        viewModel.updateUserSettings(updated)
                        onBack()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("حفظ الإعدادات")
                }

                // Account Management
                Text(
                    text = "الحساب",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )

                semaCard(
                    onClick = { viewModel.signOut() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = semaIcons.refresh(),
                            contentDescription = "تسجيل الخروج",
                            tint = ErrorRed,
                            modifier = Modifier.size(24.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "تسجيل الخروج",
                                style = MaterialTheme.typography.titleSmall,
                                color = ErrorRed
                            )
                        }
                    }
                }

                semaCard(
                    onClick = { viewModel.confirmDeleteAccount() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "حذف الحساب",
                            tint = ErrorRed,
                            modifier = Modifier.size(24.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "حذف الحساب",
                                style = MaterialTheme.typography.titleSmall,
                                color = ErrorRed
                            )
                            Text(
                                text = "سيتم حذف جميع بياناتك نهائياً",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                if (viewModel.showDeleteConfirmDialog.value) {
                    AlertDialog(
                        onDismissRequest = { viewModel.dismissDeleteConfirmDialog() },
                        title = {
                            Text(
                                text = "حذف الحساب",
                                color = TextPrimary
                            )
                        },
                        text = {
                            Text(
                                text = "هل أنت متأكد؟ سيتم حذف جميع بياناتك بشكل نهائي ولا يمكن التراجع عن هذا الإجراء.",
                                color = TextSecondary
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    viewModel.dismissDeleteConfirmDialog()
                                    viewModel.deleteAccount()
                                }
                            ) {
                                Text("حذف", color = ErrorRed)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { viewModel.dismissDeleteConfirmDialog() }) {
                                Text("إلغاء", color = TextSecondary)
                            }
                        },
                        containerColor = SurfaceVariant,
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DifficultyOption(
    label: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) PrimaryBlue.copy(alpha = 0.15f) else SurfaceVariant
    val borderColor = if (isSelected) PrimaryBlue else Outline

    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RadioButton(
                selected = isSelected,
                onClick = null,
                colors = RadioButtonDefaults.colors(
                    selectedColor = PrimaryBlue,
                    unselectedColor = TextSecondary
                )
            )
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun SettingsToggle(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = PrimaryBlue,
                checkedTrackColor = PrimaryBlue.copy(alpha = 0.3f)
            )
        )
    }
}
