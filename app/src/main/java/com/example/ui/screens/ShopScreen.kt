package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.data.UserProgressEntity
import com.example.data.GemWalletEntity
import com.example.data.HeartsStateEntity
import com.example.data.StreakFreezeInventoryEntity
import com.example.ui.semaViewModel
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.components.semaIconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    viewModel: semaViewModel,
    onBack: () -> Unit
) {
    val userProgress by viewModel.userProgress.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val gemWallet by viewModel.gemWallet.collectAsState()
    val heartsState by viewModel.heartsState.collectAsState()
    val streakFreeze by viewModel.streakFreeze.collectAsState()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "متجر sema",
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
            when {
                isLoading -> {
                    LoadingScreen()
                }
                errorMessage != null -> {
                    ErrorScreen(onRetry = { viewModel.retrySync() })
                }
                else -> {
                    userProgress?.let { progress ->
                        ShopContent(
                            viewModel = viewModel,
                            progress = progress,
                            innerPadding = innerPadding,
                            gemWallet = gemWallet,
                            heartsState = heartsState,
                            streakFreeze = streakFreeze
                        )
                    } ?: LoadingScreen()
                }
            }
        }
    }
}

@Composable
private fun ShopContent(
    viewModel: semaViewModel,
    progress: UserProgressEntity,
    innerPadding: PaddingValues,
    gemWallet: GemWalletEntity?,
    heartsState: HeartsStateEntity?,
    streakFreeze: StreakFreezeInventoryEntity?
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

        // Wallet header
        semaCard {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("الجواهر", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(
                            imageVector = semaIcons.diamond(),
                            contentDescription = "Diamond",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        AnimatedCounter(
                            targetValue = gemWallet?.balance ?: 0,
                            color = PrimaryBlue,
                            textStyle = MaterialTheme.typography.headlineMedium
                        )
                    }
                }

                Box(modifier = Modifier.width(1.dp).height(40.dp).background(SurfaceVariant))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("المجمّد", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(
                            imageVector = semaIcons.acUnit(),
                            contentDescription = "Freeze",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Text("${streakFreeze?.quantity ?: 0} / 2", style = MaterialTheme.typography.titleLarge, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }

        // Lives section
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "الصحة والقلوب",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )

            semaCard {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    semaAvatar(
                        size = 64.dp,
                        backgroundColor = ErrorRed.copy(alpha = 0.15f),
                        content = {
                            HeartVectorDrawing()
                        }
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "تعبئة القلوب",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "املأ قلوبك لمواصلة الدروس دون توقف.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                        )

                        val isHeartsFull = (heartsState?.hearts_count ?: 5) >= 5
                        val canAffordHearts = (gemWallet?.balance ?: 0) >= 350 && !isHeartsFull

                        semaButton(
                            onClick = { viewModel.rechargeHearts() },
                            enabled = canAffordHearts,
                            backgroundColor = if (canAffordHearts) PrimaryBlue else SurfaceVariant
                        ) {
                            Icon(
                                imageVector = semaIcons.favorite(),
                                contentDescription = "Hearts",
                                tint = if (canAffordHearts) TextPrimary else TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHeartsFull) "القلوب مكتملة" else "شراء لـ 350 جوهرة",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }

        // Boosters section
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "المعززات",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )

            semaCard {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    semaAvatar(
                        size = 64.dp,
                        backgroundColor = PrimaryBlue.copy(alpha = 0.15f),
                        content = {
                            Icon(
                                imageVector = semaIcons.acUnit(),
                                contentDescription = "Freeze",
                                tint = PrimaryBlue,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
Text(
                            text = "مجمّد السلسلة",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        semaBadge(
                            text = "${streakFreeze?.quantity ?: 0}/2 مفعل",
                            color = PrimaryBlue
                        )
                    }

                    Text(
                        text = "يحميك من فقدان السلسلة حتى لو فوّته يومًا!",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                        val isBoosterFull = (streakFreeze?.quantity ?: 0) >= 2
                        val canAffordBooster = (gemWallet?.balance ?: 0) >= 200 && !isBoosterFull

                        semaButton(
                            onClick = { viewModel.buyStreakFreeze() },
                            enabled = canAffordBooster,
                            backgroundColor = if (canAffordBooster) PrimaryBlue else SurfaceVariant
                        ) {
                            Icon(
                                imageVector = semaIcons.acUnit(),
                                contentDescription = "Freeze",
                                tint = if (canAffordBooster) TextPrimary else TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBoosterFull) "الحماية كاملة" else "شراء لـ 200 جوهرة",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun HeartVectorDrawing() {
    Canvas(modifier = Modifier.size(36.dp)) {
        val w = size.width
        val h = size.height

        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.25f)
            cubicTo(w * 0.35f, h * 0.1f, w * 0.05f, h * 0.1f, w * 0.05f, h * 0.45f)
            cubicTo(w * 0.05f, h * 0.7f, w * 0.3f, h * 0.85f, w * 0.5f, h * 0.95f)
            cubicTo(w * 0.7f, h * 0.85f, w * 0.95f, h * 0.7f, w * 0.95f, h * 0.45f)
            cubicTo(w * 0.95f, h * 0.1f, w * 0.65f, h * 0.1f, w * 0.5f, h * 0.25f)
            close()
        }

        drawPath(path = path, color = ErrorRed)

        drawCircle(
            color = Color.White.copy(alpha = 0.4f),
            radius = 4f,
            center = Offset(w * 0.3f, h * 0.3f)
        )
    }
}

@Composable
private fun LoadingScreen() {
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
