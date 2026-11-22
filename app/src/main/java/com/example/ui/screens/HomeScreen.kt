package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProgressEntity
import com.example.data.PhaseEntity
import com.example.data.UnitEntity
import com.example.data.LessonEntity
import com.example.data.PhaseUnlockEntity
import com.example.data.HeartsStateEntity
import com.example.data.GemWalletEntity
import com.example.data.UserStreaksEntity
import com.example.data.UserLevelsEntity
import com.example.data.StreakFreezeInventoryEntity
import com.example.data.UserWeakPointEntity
import com.example.ui.semaViewModel
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.navigation.MainDestination
import com.example.ui.components.LoadingSkeleton
import com.example.data.MilestoneTestEntity

// --- Learning Path Data ---
data class LessonNode(
    val id: Int,
    val title: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val status: NodeStatus
)

enum class NodeStatus {
    Completed,
    Active,
    Locked
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: semaViewModel,
    onStartLesson: (Int) -> Unit,
    onNavigate: (MainDestination) -> Unit
) {
    val userProgress by viewModel.userProgress.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val phases by viewModel.phases.collectAsState()
    val units by viewModel.units.collectAsState()
    val lessons by viewModel.lessons.collectAsState()
    val phaseUnlocks by viewModel.phaseUnlocks.collectAsState()
    val heartsState by viewModel.heartsState.collectAsState()
    val gemWallet by viewModel.gemWallet.collectAsState()
    val userStreaks by viewModel.userStreaks.collectAsState()
    val userLevels by viewModel.userLevels.collectAsState()
    val streakFreeze by viewModel.streakFreeze.collectAsState()

    var showStatsSheet by remember { mutableStateOf(false) }

    val bookmarkedLessonIds by viewModel.bookmarkedLessonIds.collectAsState()
    val weakPoints by viewModel.weakPoints.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchPhases()
        viewModel.fetchUnits()
        viewModel.fetchLessons()
        viewModel.fetchAllUserState()
        viewModel.fetchBookmarks()
        viewModel.fetchWeakPoints()
    }

    val nodes = run {
        val unlockedPhaseIds = phaseUnlocks.map { it.phase_id }.toMutableSet().apply { add(1) }
        val completedLessonIds = emptySet<Int>()

        val defaultIcon = semaIcons.lightMode()
        lessons.sortedBy { it.order_index }.take(20).mapIndexed { index, lesson ->
            val isPublished = lesson.is_published
            val status = when {
                !isPublished -> NodeStatus.Locked
                lesson.id in completedLessonIds -> NodeStatus.Completed
                index == 0 -> NodeStatus.Active
                else -> NodeStatus.Locked
            }
            LessonNode(
                id = lesson.id,
                title = lesson.title_ar,
                subtitle = lesson.hook_text ?: lesson.title_en,
                icon = defaultIcon,
                status = status
            )
        }.ifEmpty {
            emptyList()
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(modifier = Modifier.fillMaxSize().background(BackgroundDark)) {
            val progress = userProgress
            when {
                lessons.isNotEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        // Top bar with Friends button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "مسار التعلم",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = TextPrimary
                                )
                                semaIconButton(
                                    onClick = { onNavigate(MainDestination.Friends) },
                                    content = {
                                        Icon(
                                            imageVector = semaIcons.people(),
                                            contentDescription = "الأصدقاء",
                                            tint = TextPrimary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                )
                            }
                        }

                        // Learning path
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 100.dp),
                            contentPadding = PaddingValues(top = 72.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            itemsIndexed(nodes) { index, node ->
                                val staggerDelay = index * 50L
                                val isBookmarked = node.id in bookmarkedLessonIds
                                LearningPathNode(
                                    node = node,
                                    index = index,
                                    totalNodes = nodes.size,
                                    staggerDelay = staggerDelay,
                                    isBookmarked = isBookmarked,
                                    onClick = {
                                        if (node.status == NodeStatus.Active) {
                                            onStartLesson(node.id)
                                        }
                                    },
                                    onBookmarkClick = { viewModel.toggleBookmark(node.id) }
                                )
                            }

                            // Flashcards entry card
                            item {
                                FlashcardsEntryCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 24.dp),
                                    onClick = { onNavigate(MainDestination.FlashcardDeckOverview) }
                                )
                            }

                            // Weak points review card
                            if (weakPoints.any { it.needs_review }) {
                                item {
                                    WeakPointsReviewCard(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                        weakPointCount = weakPoints.count { it.needs_review },
                                        onClick = {
                                            val firstWeakLesson = lessons.firstOrNull()
                                            if (firstWeakLesson != null) {
                                                onStartLesson(firstWeakLesson.id)
                                            }
                                        }
                                    )
                                }
                            }

                            // Milestone test entry card
                            item {
                                val unlockedPhaseIds = phaseUnlocks.map { it.phase_id }.toMutableSet()
                                val nextTestPhase = phases.firstOrNull { phase ->
                                    phase.id != 1 && phase.id !in unlockedPhaseIds
                                } ?: phases.firstOrNull { it.id != 1 }

                                if (nextTestPhase != null) {
                                    MilestoneTestEntryCard(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                        phaseTitle = nextTestPhase.title_ar,
                                        phaseCode = nextTestPhase.code,
                                        onClick = {
                                            onNavigate(MainDestination.MilestoneTest(nextTestPhase.id))
                                        }
                                    )
                                }
                            }
                        }

                        // Floating status pill — center top
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            StatusPill(
                                streak = userStreaks?.current_streak ?: progress?.streak ?: 0,
                                hearts = heartsState?.hearts_count ?: progress?.hearts ?: 5,
                                xp = userLevels?.current_xp ?: progress?.xp ?: 0,
                                onClick = { showStatsSheet = true }
                            )
                        }
                    }

                    // Stats bottom sheet
                    if (showStatsSheet && progress != null) {
                        semaBottomSheet(onDismiss = { showStatsSheet = false }) {
                            StatsBottomSheetContent(
                                userStreaks = userStreaks,
                                heartsState = heartsState,
                                gemWallet = gemWallet,
                                userLevels = userLevels,
                                streakFreeze = streakFreeze,
                                progress = progress
                            )
                        }
                    }
                }
                errorMessage != null && lessons.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("تعذر تحميل البيانات", color = TextPrimary)
                            Spacer(Modifier.height(8.dp))
                            semaButton(onClick = { viewModel.retrySync() }) { Text("إعادة المحاولة") }
                        }
                    }
                }
                else -> {
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
            }
        }
    }
}

@Composable
private fun FlashcardsEntryCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    semaCard(
        modifier = modifier
            .fillMaxWidth(),
        highlighted = true,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(PrimaryBlue.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
Icon(
            imageVector = semaIcons.style(),
                    contentDescription = "بطاقات المراجعة",
                    tint = PrimaryBlue,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "بطاقات المراجعة",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "راجع مفرداتك وبطاقاتك المحفوظة",
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
private fun StatusPill(
    streak: Int,
    hearts: Int,
    xp: Int,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.extraLarge,
        color = SurfaceDark,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Streak (fire)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PulseAnimation {
                    Icon(
                        imageVector = semaIcons.localFireDepartment(),
                        contentDescription = "Streak",
                        tint = XpYellow,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = streak.toString(),
                    color = TextPrimary,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            // Hearts
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = semaIcons.favorite(),
                    contentDescription = "Hearts",
                    tint = ErrorRed,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = hearts.toString(),
                    color = TextPrimary,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            // XP
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = semaIcons.flashOn(),
                    contentDescription = "XP",
                    tint = XpYellow,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = xp.toString(),
                    color = TextPrimary,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun StatsBottomSheetContent(
    userStreaks: UserStreaksEntity?,
    heartsState: HeartsStateEntity?,
    gemWallet: GemWalletEntity?,
    userLevels: UserLevelsEntity?,
    streakFreeze: StreakFreezeInventoryEntity?,
    progress: UserProgressEntity?
) {
    Text(
        text = "إحصائياتك",
        style = MaterialTheme.typography.headlineSmall,
        color = TextPrimary
    )

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatTile(
            icon = semaIcons.localFireDepartment(),
            label = "السلسلة",
            value = "${userStreaks?.current_streak ?: progress?.streak ?: 0} يوم",
            color = XpYellow,
            modifier = Modifier.weight(1f)
        )
        StatTile(
            icon = semaIcons.favorite(),
            label = "القلوب",
            value = "${heartsState?.hearts_count ?: progress?.hearts ?: 5} / 5",
            color = ErrorRed,
            modifier = Modifier.weight(1f)
        )
        StatTile(
            icon = semaIcons.flashOn(),
            label = "الخبرة",
            value = "${userLevels?.current_xp ?: progress?.xp ?: 0} XP",
            color = PrimaryBlue,
            modifier = Modifier.weight(1f)
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatTile(
            icon = semaIcons.diamond(),
            label = "الجواهر",
            value = "${gemWallet?.balance ?: progress?.gems ?: 0}",
            color = PrimaryBlue,
            modifier = Modifier.weight(1f)
        )
        StatTile(
            icon = semaIcons.acUnit(),
            label = "المجمّد",
            value = "${streakFreeze?.quantity ?: progress?.streakFreezeCount ?: 0} / 2",
            color = PrimaryBlue,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StatTile(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    semaCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = value,
                color = color,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = label,
                color = TextSecondary,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun WeakPointsReviewCard(
    modifier: Modifier = Modifier,
    weakPointCount: Int,
    onClick: () -> Unit
) {
    semaCard(
        modifier = modifier,
        highlighted = true,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(ErrorRed.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = semaIcons.refresh(),
                    contentDescription = "نقاط الضعف",
                    tint = ErrorRed,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ركز على نقاط ضعفك",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "لديك $weakPointCount مهارات تحتاج مراجعة. ابدأ درساً لتقويتها.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2,
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
private fun MilestoneTestEntryCard(
    modifier: Modifier = Modifier,
    phaseTitle: String,
    phaseCode: String,
    onClick: () -> Unit
) {
    semaCard(
        modifier = modifier,
        highlighted = true,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(XpYellow.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = semaIcons.graduationCap(),
                    contentDescription = "اختبار المرحلة",
                    tint = XpYellow,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "اختبار المرحلة: $phaseTitle",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "اجتز الاختبار لفتح المرحلة التالية ($phaseCode)",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2,
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
private fun LearningPathNode(
    node: LessonNode,
    index: Int,
    totalNodes: Int,
    staggerDelay: Long,
    isBookmarked: Boolean = false,
    onClick: () -> Unit,
    onBookmarkClick: () -> Unit = {}
) {
    // Entrance animation
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(staggerDelay)
        visible = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "node_alpha"
    )

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "node_scale"
    )

    // Active node pulse
    val pulseOffset = remember { Animatable(0f) }
    LaunchedEffect(node.status) {
        if (node.status == NodeStatus.Active) {
            while (true) {
                pulseOffset.animateTo(-6f, tween(600, easing = FastOutSlowInEasing))
                pulseOffset.animateTo(0f, tween(600, easing = FastOutSlowInEasing))
            }
        }
    }

    val nodeColor = when (node.status) {
        NodeStatus.Completed -> SuccessGreen
        NodeStatus.Active -> PrimaryBlue
        NodeStatus.Locked -> TextSecondary.copy(alpha = 0.3f)
    }

    val iconColor = when (node.status) {
        NodeStatus.Completed -> TextPrimary
        NodeStatus.Active -> TextPrimary
        NodeStatus.Locked -> TextSecondary.copy(alpha = 0.5f)
    }

    // Winding path offset
    val xOffset = when {
        index % 4 == 0 -> (-40).dp
        index % 4 == 1 -> 0.dp
        index % 4 == 2 -> 40.dp
        else -> 0.dp
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset(x = xOffset)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Active badge
            if (node.status == NodeStatus.Active) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryBlue)
                        .border(BorderStroke(2.dp, TextPrimary), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "ابدأ الدرس",
                        color = TextPrimary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // Node circle
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .graphicsLayer {
                        translationY = if (node.status == NodeStatus.Active) pulseOffset.value else 0f
                    }
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                nodeColor,
                                nodeColor.copy(alpha = 0.8f)
                            )
                        )
                    )
                    .then(
                        if (node.status == NodeStatus.Active) {
                            Modifier.border(3.dp, TextPrimary, CircleShape)
                        } else Modifier
                    )
                    .clickable(
                        enabled = node.status != NodeStatus.Locked,
                        onClick = onClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = node.icon,
                    contentDescription = node.title,
                    tint = iconColor,
                    modifier = Modifier.size(36.dp)
                )
            }

            // Label
            Text(
                text = node.title,
                color = when (node.status) {
                    NodeStatus.Locked -> TextSecondary.copy(alpha = 0.5f)
                    else -> TextPrimary
                },
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
                fontWeight = if (node.status == NodeStatus.Active) FontWeight.Black else FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (node.status != NodeStatus.Locked) {
                Text(
                    text = node.subtitle,
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (node.status != NodeStatus.Locked) {
                IconButton(
                    onClick = onBookmarkClick,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isBookmarked) semaIcons.star() else semaIcons.star(),
                        contentDescription = if (isBookmarked) "إزالة من المفضلة" else "إضافة للمفضلة",
                        tint = if (isBookmarked) XpYellow else TextSecondary.copy(alpha = 0.4f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Connector line (not on last node)
            if (index < totalNodes - 1) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(24.dp)
                        .background(
                            when (node.status) {
                                NodeStatus.Completed -> SuccessGreen.copy(alpha = 0.5f)
                                NodeStatus.Active -> PrimaryBlue.copy(alpha = 0.5f)
                                NodeStatus.Locked -> TextSecondary.copy(alpha = 0.2f)
                            }
                        )
                )
            }
        }
    }
}
