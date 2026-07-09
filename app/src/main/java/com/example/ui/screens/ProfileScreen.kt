package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.LingoMindViewModel
import com.example.ui.components.TactileButton
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: LingoMindViewModel
) {
    val userProgress by viewModel.userProgress.collectAsState()
    val progress = userProgress ?: return

    var showSettingsDialog by remember { mutableStateOf(false) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "الملف الشخصي",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = OnSurfaceLight
                        )
                    },
                    actions = {
                        IconButton(onClick = { showSettingsDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "الإعدادات",
                                tint = OnSurfaceLight
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = DarkBackground
                    )
                )
            },
            containerColor = DarkBackground
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Avatar and username Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(RoyalCobalt),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "User Profile",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = progress.username,
                            color = OnSurfaceLight,
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp
                        )

                        Text(
                            text = progress.email,
                            color = OnSurfaceVariantLight,
                            fontSize = 13.sp
                        )

                        Text(
                            text = "تاريخ الانضمام: ${progress.joinedDate}",
                            color = OnSurfaceVariantLight.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                }

                // Followers count row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Group,
                        contentDescription = "Followers",
                        tint = RoyalCobalt,
                        modifier = Modifier.size(18.dp)
                    )

                    Text(
                        text = "1 متابِع",
                        color = OnSurfaceLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Text(
                        text = "•",
                        color = OnSurfaceVariantLight,
                        fontSize = 14.sp
                    )

                    Text(
                        text = "0 يتابعهم",
                        color = OnSurfaceVariantLight,
                        fontSize = 14.sp
                    )
                }

                // Statistics title
                Text(
                    text = "الإحصائيات الإجمالية",
                    color = OnSurfaceLight,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )

                // 2x2 Stats Dashboard Grid
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Stat 1: Streak (Whatshot SVG)
                        StatCard(
                            label = "سلسلة الحماسة",
                            value = "${progress.streak} أيام",
                            iconVector = Icons.Filled.Whatshot,
                            tintColor = GildedAmber,
                            modifier = Modifier.weight(1f)
                        )

                        // Stat 2: XP (FlashOn SVG)
                        StatCard(
                            label = "نقاط الخبرة",
                            value = "${progress.xp} XP",
                            iconVector = Icons.Filled.FlashOn,
                            tintColor = DiamondBlue,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Stat 3: League (EmojiEvents SVG)
                        StatCard(
                            label = "الدوري الحالي",
                            value = "الذهبي",
                            iconVector = Icons.Filled.EmojiEvents,
                            tintColor = GildedAmber,
                            modifier = Modifier.weight(1f)
                        )

                        // Stat 4: Top Rank (WorkspacePremium SVG)
                        StatCard(
                            label = "أفضل المراكز",
                            value = "المركز الأول",
                            iconVector = Icons.Filled.WorkspacePremium,
                            tintColor = RoyalCobalt,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Achievements section
                Text(
                    text = "إنجازات الملف الشخصي",
                    color = OnSurfaceLight,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Achievement 1: Streak
                    AchievementItem(
                        title = "البرية (Wildfire)",
                        description = "حقق سلسلة حماسة لمدة 10 أيام متتالية",
                        progressFraction = progress.streak.toFloat() / 10f,
                        progressText = "${progress.streak} / 10 يوم",
                        badgeIcon = Icons.Filled.Whatshot,
                        badgeTint = GildedAmber
                    )

                    // Achievement 2: XP Champion
                    AchievementItem(
                        title = "بطل المعرفة (Champion)",
                        description = "اكتسب ما مجموعه 1000 نقطة خبرة",
                        progressFraction = progress.xp.toFloat() / 1000f,
                        progressText = "${progress.xp} / 1000 XP",
                        badgeIcon = Icons.Filled.WorkspacePremium,
                        badgeTint = Color(0xFFFBBF24)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            // Settings modal dialog Overlay
            if (showSettingsDialog) {
                SettingsPanelDialog(
                    viewModel = viewModel,
                    soundEnabled = progress.soundEffectsEnabled,
                    animEnabled = progress.animationsEnabled,
                    notifEnabled = progress.notificationsEnabled,
                    onDismiss = { showSettingsDialog = false }
                )
            }
        }
    }
}

// --- Stat Card Component ---
@Composable
fun StatCard(
    label: String,
    value: String,
    iconVector: ImageVector,
    tintColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = label,
                    tint = tintColor,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = value,
                    color = tintColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
            }

            Text(
                text = label,
                color = OnSurfaceVariantLight,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// --- Achievement Card item ---
@Composable
fun AchievementItem(
    title: String,
    description: String,
    progressFraction: Float,
    progressText: String,
    badgeIcon: ImageVector,
    badgeTint: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(badgeTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = badgeIcon,
                    contentDescription = title,
                    tint = badgeTint,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    color = OnSurfaceLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = description,
                    color = OnSurfaceVariantLight,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Custom Linear progress bar
                val currentProgress = progressFraction.coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceHigh)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(currentProgress)
                            .clip(CircleShape)
                            .background(RoyalCobalt)
                    )
                    Text(
                        text = progressText,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 9.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

// --- Settings Dialog Composable ---
@Composable
fun SettingsPanelDialog(
    viewModel: LingoMindViewModel,
    soundEnabled: Boolean,
    animEnabled: Boolean,
    notifEnabled: Boolean,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = DarkSurface,
            border = BorderStroke(2.dp, Color.White.copy(alpha = 0.08f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "إعدادات LingoMind",
                    color = OnSurfaceLight,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Toggle 1: Sound
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("الأصوات التفاعلية", color = OnSurfaceLight, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("تشغيل مؤثرات النصر والخطأ", color = OnSurfaceVariantLight, fontSize = 11.sp)
                    }
                    Switch(
                        checked = soundEnabled,
                        onCheckedChange = { viewModel.updateUserSettings(it, animEnabled, notifEnabled) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = RoyalCobalt,
                            checkedTrackColor = RoyalCobalt.copy(alpha = 0.3f)
                        )
                    )
                }

                Divider(color = Color.White.copy(alpha = 0.05f))

                // Toggle 2: Animations
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("الرسوم والحركات بصرية", color = OnSurfaceLight, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("تأثيرات اهتزاز وبثق الأزرار", color = OnSurfaceVariantLight, fontSize = 11.sp)
                    }
                    Switch(
                        checked = animEnabled,
                        onCheckedChange = { viewModel.updateUserSettings(soundEnabled, it, notifEnabled) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = RoyalCobalt,
                            checkedTrackColor = RoyalCobalt.copy(alpha = 0.3f)
                        )
                    )
                }

                Divider(color = Color.White.copy(alpha = 0.05f))

                // Toggle 3: Notifications
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("إشعارات التذكير اليومية", color = OnSurfaceLight, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("تنبيهات للمحافظة على سلسلة الحماسة", color = OnSurfaceVariantLight, fontSize = 11.sp)
                    }
                    Switch(
                        checked = notifEnabled,
                        onCheckedChange = { viewModel.updateUserSettings(soundEnabled, animEnabled, it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = RoyalCobalt,
                            checkedTrackColor = RoyalCobalt.copy(alpha = 0.3f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Wipe/Reset all Progress button
                Button(
                    onClick = {
                        viewModel.resetProgress()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DuolingoRed),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Reset Progress",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "إعادة ضبط جميع الإحصائيات",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                }

                // Close Settings
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "إغلاق",
                        color = OnSurfaceVariantLight,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
