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
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.LingoMindViewModel
import com.example.ui.components.TactileButton
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestsScreen(
    viewModel: LingoMindViewModel,
    onAuthClick: () -> Unit
) {
    val userProgress by viewModel.userProgress.collectAsState()
    val totalXp = userProgress?.xp ?: 887

    // Daily quest calculations
    val progressPercent = if (totalXp > 0) 1f else 0f

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "المهام اليومية",
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Leagues Lock Card
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
                                .size(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurfaceHigh)
                                .border(BorderStroke(2.dp, GildedAmber), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.EmojiEvents,
                                contentDescription = "Trophy",
                                tint = GildedAmber,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "دوري اللغات مفتوح!",
                                color = GildedAmber,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "أكمل الدروس اليومية وتنافس مع زملائك على الصدارة.",
                                color = OnSurfaceVariantLight,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // Daily Quest Progress Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                ) {
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
                                text = "مهمة اليوم",
                                color = OnSurfaceLight,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )

                            Text(
                                text = "مكتملة",
                                color = DuolingoGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(GildedAmber.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.FlashOn,
                                    contentDescription = "Energy",
                                    tint = GildedAmber,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "اكتسب 10 نقاط خبرة (XP)",
                                    color = OnSurfaceLight,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )

                                // Custom styled linear progress indicator
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(18.dp)
                                        .clip(CircleShape)
                                        .background(DarkSurfaceHigh)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(progressPercent)
                                            .clip(CircleShape)
                                            .background(GildedAmber)
                                    )
                                    Text(
                                        text = if (totalXp > 0) "10 / 10" else "0 / 10",
                                        color = if (totalXp > 0) DarkBackground else OnSurfaceVariantLight,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                        }
                    }
                }

                // Profile Connection / Creation CTA Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(2.dp, Color.White.copy(alpha = 0.08f))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(RoyalCobalt.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CardGiftcard,
                                contentDescription = "Gift",
                                tint = GildedAmber,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Text(
                            text = "أنشئ ملفًا شخصيًا لحفظ تقدمك وتنافسك في LingoMind!",
                            color = OnSurfaceLight,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "من خلال تسجيل الدخول، ستحافظ على سجل الدروس والمكافآت التي اكتسبتها دائمًا.",
                            color = OnSurfaceVariantLight,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        TactileButton(
                            onClick = onAuthClick,
                            backgroundColor = RoyalCobalt,
                            shadowColor = Color(0xFF003380),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "إنشاء ملف شخصي",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }

                        TactileButton(
                            onClick = onAuthClick,
                            backgroundColor = GlacierMist,
                            shadowColor = Color(0xFF9ABED6),
                            contentColor = RoyalCobalt,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "تسجيل الدخول",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
