package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.LingoMindViewModel
import com.example.ui.components.TactileButton
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    viewModel: LingoMindViewModel
) {
    val userProgress by viewModel.userProgress.collectAsState()
    val progress = userProgress ?: return

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "متجر LingoMind",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = OnSurfaceLight
                        )
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

                // Wallet Balances header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurface)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("رصيد الجواهر", color = OnSurfaceVariantLight, fontSize = 12.sp)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(
                                imageVector = Icons.Filled.Diamond,
                                contentDescription = "Diamond",
                                tint = DiamondBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Text("${progress.gems}", color = DiamondBlue, fontWeight = FontWeight.Black, fontSize = 20.sp)
                        }
                    }

                    Box(modifier = Modifier.width(1.dp).height(40.dp).background(DarkSurfaceHigh))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("مجمّد السلسلة", color = OnSurfaceVariantLight, fontSize = 12.sp)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(
                                imageVector = Icons.Filled.AcUnit,
                                contentDescription = "Freeze",
                                tint = DiamondBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Text("${progress.streakFreezeCount} / 2", color = OnSurfaceLight, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }
                }

                // Section: Lives
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "الصحة والقلوب",
                        color = OnSurfaceLight,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )

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
                            // Canvas Heart Icon with glowing outline
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(DarkSurfaceHigh),
                                contentAlignment = Alignment.Center
                            ) {
                                HeartVectorDrawing()
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "تعبئة القلوب بالكامل",
                                    color = OnSurfaceLight,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "املأ قلوبك لزيادة فرصة ارتكاب الأخطاء والتعلم في دروسك دون توقف.",
                                    color = OnSurfaceVariantLight,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                                )

                                val isHeartsFull = progress.hearts >= 5
                                val canAffordHearts = progress.gems >= 350 && !isHeartsFull

                                TactileButton(
                                    onClick = { viewModel.rechargeHearts() },
                                    enabled = canAffordHearts,
                                    backgroundColor = if (canAffordHearts) RoyalCobalt else Color(0xFF1E3A63),
                                    shadowColor = if (canAffordHearts) Color(0xFF003380) else Color(0xFF0B1930),
                                    contentColor = Color.White
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Favorite,
                                        contentDescription = "Hearts",
                                        tint = if (canAffordHearts) Color.White else OnSurfaceVariantLight,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isHeartsFull) "الصحة مكتملة" else "شراء لـ 350 جوهرة",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Section: Boosters (Gel de série)
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "المعززات والحماية",
                        color = OnSurfaceLight,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )

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
                            // Ice crystal vector
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(DarkSurfaceHigh),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AcUnit,
                                    contentDescription = "Freeze",
                                    tint = DiamondBlue,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "مجمّد السلسلة (Gel)",
                                        color = OnSurfaceLight,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(RoyalCobalt.copy(alpha = 0.15f))
                                            .border(BorderStroke(1.dp, RoyalCobalt), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "${progress.streakFreezeCount}/2 مفعل",
                                            color = RoyalCobalt,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Text(
                                    text = "يحميك من فقدان السلسلة اليومية حتى لو فوّت التدريب ليوم واحد!",
                                    color = OnSurfaceVariantLight,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                                )

                                val isBoosterFull = progress.streakFreezeCount >= 2
                                val canAffordBooster = progress.gems >= 200 && !isBoosterFull

                                TactileButton(
                                    onClick = { viewModel.buyStreakFreeze() },
                                    enabled = canAffordBooster,
                                    backgroundColor = if (canAffordBooster) RoyalCobalt else Color(0xFF1E3A63),
                                    shadowColor = if (canAffordBooster) Color(0xFF003380) else Color(0xFF0B1930),
                                    contentColor = Color.White
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.AcUnit,
                                        contentDescription = "Freeze",
                                        tint = if (canAffordBooster) Color.White else OnSurfaceVariantLight,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isBoosterFull) "الحماية كاملة" else "شراء لـ 200 جوهرة",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// --- Dynamic Canvas Heart drawing with pulse simulation ---
@Composable
fun HeartVectorDrawing() {
    Canvas(modifier = Modifier.size(40.dp)) {
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

        drawPath(
            path = path,
            color = DuolingoRed
        )

        // Draw small inner shine spot
        drawCircle(
            color = Color.White.copy(alpha = 0.4f),
            radius = 4f,
            center = Offset(w * 0.3f, h * 0.3f)
        )
    }
}
