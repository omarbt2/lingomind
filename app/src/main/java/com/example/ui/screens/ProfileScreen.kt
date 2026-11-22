package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.data.UserProgressEntity
import com.example.data.HeartsStateEntity
import com.example.data.GemWalletEntity
import com.example.data.UserStreaksEntity
import com.example.data.UserLevelsEntity
import com.example.data.StreakFreezeInventoryEntity
import com.example.ui.semaViewModel
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.navigation.MainDestination
import com.example.ui.components.LoadingSkeleton
import com.example.ui.components.HapticHelper
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: semaViewModel,
    onNavigate: (MainDestination) -> Unit
) {
    val userProgress by viewModel.userProgress.collectAsState()
    val heartsState by viewModel.heartsState.collectAsState()
    val gemWallet by viewModel.gemWallet.collectAsState()
    val userStreaks by viewModel.userStreaks.collectAsState()
    val userLevels by viewModel.userLevels.collectAsState()
    val streakFreeze by viewModel.streakFreeze.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchAllUserState()
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "الملف الشخصي",
                            style = MaterialTheme.typography.headlineSmall,
                            color = TextPrimary
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = BackgroundDark
                    )
                )
            },
            containerColor = BackgroundDark
        ) { innerPadding ->
            when {
                userProgress != null -> {
                    val progress = userProgress!!
                    ProfileContent(
                        viewModel = viewModel,
                        progress = progress,
                        heartsState = heartsState,
                        gemWallet = gemWallet,
                        userStreaks = userStreaks,
                        userLevels = userLevels,
                        streakFreeze = streakFreeze,
                        innerPadding = innerPadding,
                        onSettingsClick = { onNavigate(MainDestination.Settings) },
                        onNavigate = onNavigate
                    )
                }
                isLoading -> {
                    LoadingScreen()
                }
                errorMessage != null -> {
                    ErrorScreen(onRetry = { viewModel.retrySync() })
                }
                else -> {
                    LoadingScreen()
                }
            }
        }
    }
}

@Composable
private fun ProfileContent(
    viewModel: semaViewModel,
    progress: UserProgressEntity,
    heartsState: HeartsStateEntity?,
    gemWallet: GemWalletEntity?,
    userStreaks: UserStreaksEntity?,
    userLevels: UserLevelsEntity?,
    streakFreeze: StreakFreezeInventoryEntity?,
    innerPadding: PaddingValues,
    onSettingsClick: () -> Unit,
    onNavigate: (MainDestination) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Avatar and username Header
        semaCard {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                semaAvatar(size = 80.dp)

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = progress.username,
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = progress.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "تاريخ الانضمام: ${progress.joinedDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Stats grid
        Text(
            text = "الإحصائيات",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            StaggeredEntrance(index = 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    label = "السلسلة",
                    value = "${userStreaks?.current_streak ?: progress.streak} أيام",
                    iconVector = semaIcons.localFireDepartment(),
                    tintColor = XpYellow,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigate(MainDestination.StreakCalendar) },
                    onClick = { onNavigate(MainDestination.StreakCalendar) }
                )
                StatCard(
                    label = "الخبرة",
                    value = "${userLevels?.current_xp ?: progress.xp} XP",
                    iconVector = semaIcons.flashOn(),
                    tintColor = PrimaryBlue,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigate(MainDestination.Recap) },
                    onClick = { onNavigate(MainDestination.Recap) }
                )
            }
            }
            StaggeredEntrance(index = 1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    label = "الدوري",
                    value = progress.league,
                    iconVector = semaIcons.emojiEvents(),
                    tintColor = XpYellow,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "أفضل مركز",
                    value = progress.bestRank,
                    iconVector = semaIcons.workspacePremium(),
                    tintColor = PrimaryBlue,
                    modifier = Modifier.weight(1f)
                )
            }
            }
        }

        // Achievements section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "الإنجازات",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            Text(
                text = "عرض الكل",
                color = PrimaryBlue,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(MainDestination.AchievementsList) }
                    .padding(end = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.End
            )
        }

        semaCard {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StaggeredEntrance(index = 0) {
                AchievementItem(
                    title = "السلسلة النارية",
                    description = "حقق سلسلة 10 أيام متتالية",
                    progressFraction = (userStreaks?.current_streak ?: progress.streak).toFloat() / 10f,
                    progressText = "${userStreaks?.current_streak ?: progress.streak} / 10",
                    badgeIcon = semaIcons.localFireDepartment(),
                    badgeTint = XpYellow
                )
                }
                StaggeredEntrance(index = 1) {
                AchievementItem(
                    title = "بطل المعرفة",
                    description = "اكتسب 1000 نقطة خبرة",
                    progressFraction = (userLevels?.current_xp ?: progress.xp).toFloat() / 1000f,
                    progressText = "${userLevels?.current_xp ?: progress.xp} / 1000 XP",
                    badgeIcon = semaIcons.workspacePremium(),
                    badgeTint = PrimaryBlue
                )
                }
            }
        }

        // Account actions section
        Text(
            text = "الحساب",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary
        )

        semaCard {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SettingsRow(
                    icon = semaIcons.settings(),
                    title = "الإعدادات",
                    subtitle = "أهداف التعلم، الصوت، الخصوصية",
                    onClick = { onNavigate(MainDestination.Settings) }
                )
                SettingsRow(
                    icon = semaIcons.star(),
                    title = "الدروس المحفوظة",
                    subtitle = "عرض الدروس التي حفظتها",
                    onClick = { onNavigate(MainDestination.Bookmarks) }
                )
                SettingsRow(
                    icon = semaIcons.diamond(),
                    title = "سجل الجواهر",
                    subtitle = "جميع معاملات الجواهر",
                    onClick = { onNavigate(MainDestination.GemHistory) }
                )
                SettingsRow(
                    icon = semaIcons.person(),
                    title = "تعديل الملف الشخصي",
                    subtitle = "تغيير الاسم والبريد الإلكتروني",
                    onClick = { onNavigate(MainDestination.EditProfile) }
                )
                SettingsRow(
                    icon = semaIcons.translate(),
                    title = "المساعد الذكي",
                    subtitle = "تدرب على المحادثة مع AI",
                    onClick = { onNavigate(MainDestination.AIChat) }
                )
                SettingsRow(
                    icon = semaIcons.style(),
                    title = "تمرين الكتابة",
                    subtitle = "حسّن مهاراتك في الكتابة",
                    onClick = { onNavigate(MainDestination.Writing) }
                )
                SettingsRow(
                    icon = semaIcons.emojiEvents(),
                    title = "التحديات اليومية",
                    subtitle = "التحديات والجوائز اليومية",
                    onClick = { onNavigate(MainDestination.DailyChallenges) }
                )
                SettingsRow(
                    icon = semaIcons.bell(),
                    title = "الإشعارات",
                    subtitle = "تخصيص التنبيهات والإشعارات",
                    onClick = { onNavigate(MainDestination.NotificationSettings) }
                )
                SettingsRow(
                    icon = semaIcons.email(),
                    title = "الدعم",
                    subtitle = "مركز المساعدة والتواصل",
                    onClick = { onNavigate(MainDestination.Support) }
                )
                SettingsRow(
                    icon = semaIcons.graduationCap(),
                    title = "عن Sema",
                    subtitle = "قصة التطبيق ورؤيتنا",
                    onClick = { onNavigate(MainDestination.About) }
                )
                SettingsRow(
                    icon = semaIcons.refresh(),
                    title = "إعادة ضبط الإحصائيات",
                    subtitle = "إرجاع كل الإحصائيات للقيم الافتراضية",
                    isDestructive = true,
                    onClick = { viewModel.resetProgress() }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    iconVector: ImageVector,
    tintColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    semaCard(
        modifier = modifier,
        highlighted = onClick != null,
        onClick = onClick
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
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    semaCard(
        highlighted = isDestructive,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(if (isDestructive) ErrorRed.copy(alpha = 0.15f) else PrimaryBlue.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isDestructive) ErrorRed else PrimaryBlue,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isDestructive) ErrorRed else TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = semaIcons.chevronLeft(),
                contentDescription = "التالي",
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun AchievementItem(
    title: String,
    description: String,
    progressFraction: Float,
    progressText: String,
    badgeIcon: ImageVector,
    badgeTint: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        semaAvatar(
            size = 48.dp,
            backgroundColor = badgeTint.copy(alpha = 0.15f),
            content = {
                Icon(
                    imageVector = badgeIcon,
                    contentDescription = title,
                    tint = badgeTint,
                    modifier = Modifier.size(24.dp)
                )
            }
        )

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            semaProgressBar(
                progress = progressFraction,
                label = progressText,
                color = badgeTint,
                modifier = Modifier.height(14.dp)
            )
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LoadingSkeleton(modifier = Modifier.width(80.dp).height(80.dp).clip(CircleShape))
            LoadingSkeleton(modifier = Modifier.width(120.dp).height(24.dp))
            LoadingSkeleton(modifier = Modifier.width(80.dp).height(16.dp))
        }
    }
}

@Composable
private fun ErrorScreen(onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("تعذر تحميل البيانات", color = TextPrimary)
            Spacer(Modifier.height(8.dp))
            semaButton(onClick = onRetry) { Text("إعادة المحاولة") }
        }
    }
}
