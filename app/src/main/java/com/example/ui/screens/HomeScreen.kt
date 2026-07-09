package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.LingoMindViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: LingoMindViewModel,
    onStartLesson: (Int) -> Unit
) {
    val userProgress by viewModel.userProgress.collectAsState()
    val progress = userProgress ?: return

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = "LingoMind",
                                tint = RoyalCobalt,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = "LingoMind",
                                color = RoyalCobalt,
                                fontWeight = FontWeight.Black,
                                fontSize = 24.sp
                            )
                        }
                    },
                    actions = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            // Streak count (Fire SVG replace)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = progress.streak.toString(),
                                    color = GildedAmber,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Icon(
                                    imageVector = Icons.Filled.Whatshot,
                                    contentDescription = "أيام متتالية",
                                    tint = GildedAmber,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Gems count (Diamond SVG replace)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = progress.gems.toString(),
                                    color = DiamondBlue,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Icon(
                                    imageVector = Icons.Filled.Diamond,
                                    contentDescription = "مجوهرات",
                                    tint = DiamondBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Hearts count (Heart SVG replace)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = progress.hearts.toString(),
                                    color = DuolingoRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Icon(
                                    imageVector = Icons.Filled.Favorite,
                                    contentDescription = "قلوب",
                                    tint = DuolingoRed,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DarkBackground,
                        titleContentColor = OnSurfaceLight
                    )
                )
            },
            containerColor = DarkBackground
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 32.dp, top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Chapter Header Card
                    item {
                        ChapterHeaderCard()
                    }

                    // Redesigned stacked capsule units as per user's gradient blue design image!
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy((-16).dp), // Slight overlaps for that beautiful piled/stacked look
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val units = listOf(
                                Triple("القسم 1: مقدمة الحروف", "تعلم أساسيات نطق الكلمات والتحية", BlueShade1),
                                Triple("القسم 2: القهوة والمطاعم", "طلب المأكولات والتحدث مع النادل", BlueShade2),
                                Triple("القسم 3: الروتين واليوميات", "التحدث عن يومك والأنشطة المعتادة", BlueShade3),
                                Triple("القسم 4: السفر والتنقل", "طلب الاتجاهات وحجز التذاكر بسهولة", BlueShade4),
                                Triple("القسم 5: التسوق والملابس", "السؤال عن الأسعار ووصف الملابس", BlueShade5),
                                Triple("القسم 6: الصحة والطقس", "وصف الطقس والتعبير عن التعب والراحة", BlueShade6),
                                Triple("القسم 7: التعليم والعمل", "الحديث عن المهن والمهارات الأساسية", BlueShade7),
                                Triple("القسم 8: قواعد المحادثة", "تركيب جمل معقدة والتعبير عن الرأي", BlueShade8),
                                Triple("القسم 9: المصطلحات الشائعة", "تعلم الأمثال والتعبيرات اليومية الدارجة", BlueShade9),
                                Triple("القسم 10: المراجعة النهائية", "اختبار شامل لجميع الأقسام السابقة", BlueShade10),
                                Triple("القسم 11: التحدث بطلاقة", "محادثات متقدمة واختبار مهارة النطق", BlueShade11),
                                Triple("القسم 12: حفل التخرج والوسام", "احصل على شهادة إنهاء المسار التعليمي", BlueShade12)
                            )

                            units.forEachIndexed { index, unit ->
                                val unitId = index + 1
                                val isCompleted = unitId < 4 // Units 1, 2, 3 are completed
                                val isActive = unitId == 4    // Unit 4 (Food & Cafe ordering) is active
                                val isLocked = unitId > 4     // Rest are locked

                                val status = when {
                                    isCompleted -> NodeStatus.Completed
                                    isActive -> NodeStatus.Active
                                    else -> NodeStatus.Locked
                                }

                                GradientCapsuleUnitButton(
                                    title = unit.first,
                                    subtitle = unit.second,
                                    backgroundColor = unit.third,
                                    status = status,
                                    onClick = {
                                        if (status == NodeStatus.Active) {
                                            onStartLesson(1)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- Chapter Header Card Composable ---
@Composable
fun ChapterHeaderCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(RoyalCobalt, Color(0xFF1E4E78))
                )
            )
            .border(BorderStroke(2.dp, Color.White.copy(alpha = 0.15f)), RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "المستوى الحالي • القسم 1",
                    color = GlacierMist,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "طلب القهوة وتناول الطعام في الخارج",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.LocalCafe,
                    contentDescription = "طلب القهوة",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

// --- Node Status ---
enum class NodeStatus {
    Completed,
    Active,
    Locked
}

// --- Redesigned Gradient Capsule Unit Button ---
@Composable
fun GradientCapsuleUnitButton(
    title: String,
    subtitle: String,
    backgroundColor: Color,
    status: NodeStatus,
    onClick: () -> Unit
) {
    val isLightBg = backgroundColor.red + backgroundColor.green + backgroundColor.blue > 1.5f
    val contentColor = if (isLightBg) Color(0xFF020B1C) else Color.White
    val subContentColor = if (isLightBg) Color(0xFF334155) else Color.White.copy(alpha = 0.7f)

    val bounceOffset = remember { Animatable(0f) }
    
    if (status == NodeStatus.Active) {
        LaunchedEffect(Unit) {
            while (true) {
                bounceOffset.animateTo(
                    targetValue = -10f,
                    animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
                )
                bounceOffset.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = bounceOffset.value.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (status == NodeStatus.Active) {
            // "ابدأ" floating badge
            Box(
                modifier = Modifier
                    .offset(y = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(RoyalCobalt)
                    .border(BorderStroke(2.dp, Color.White), RoundedCornerShape(8.dp))
                    .padding(horizontal = 14.dp, vertical = 4.dp)
                    .zIndex(10f)
            ) {
                Text(
                    text = "ابدأ الدرس الحالي",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(backgroundColor)
                .clickable(
                    enabled = status != NodeStatus.Locked,
                    onClick = onClick,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = LocalIndication.current
                )
                .border(
                    BorderStroke(
                        width = if (status == NodeStatus.Active) 3.dp else 1.dp,
                        color = when (status) {
                            NodeStatus.Active -> RoyalCobalt
                            else -> Color.White.copy(alpha = 0.15f)
                        }
                    ),
                    RoundedCornerShape(32.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = title,
                        color = contentColor,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                    Text(
                        text = subtitle,
                        color = subContentColor,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                }

                // Status Icon (SVG instead of Emojis)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            when (status) {
                                NodeStatus.Completed -> Color(0xFF58CC02)
                                NodeStatus.Active -> RoyalCobalt
                                NodeStatus.Locked -> Color.Black.copy(alpha = 0.2f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (status) {
                            NodeStatus.Completed -> Icons.Filled.Check
                            NodeStatus.Active -> Icons.Filled.PlayArrow
                            NodeStatus.Locked -> Icons.Filled.Lock
                        },
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
