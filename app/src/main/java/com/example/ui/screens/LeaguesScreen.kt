package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.LingoMindViewModel
import com.example.ui.components.TactileButton
import com.example.ui.theme.*

// Mock data model for competitors using clean Vector avatars
data class Competitor(
    val username: String,
    val xp: Int,
    val avatarIcon: ImageVector,
    val avatarTint: Color,
    val isCurrentUser: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaguesScreen(
    viewModel: LingoMindViewModel,
    onStartLesson: () -> Unit
) {
    val userProgress by viewModel.userProgress.collectAsState()
    val currentXp = userProgress?.xp ?: 887

    val competitors = listOf(
        Competitor("boutalbi", 23590, Icons.Filled.AccountCircle, Color(0xFFF87171)),
        Competitor("Boutalbi", 3878, Icons.Filled.Person, Color(0xFF60A5FA)),
        Competitor("Omar (أنت)", currentXp, Icons.Filled.Person, RoyalCobalt, isCurrentUser = true),
        Competitor("boutalbi_mini", 1964, Icons.Filled.AccountCircle, Color(0xFF34D399)),
        Competitor("Sami", 1432, Icons.Filled.Person, Color(0xFFFBBF24)),
        Competitor("Sarah", 950, Icons.Filled.AccountCircle, Color(0xFFF472B6)),
        Competitor("Fatima", 540, Icons.Filled.Person, Color(0xFFA78BFA))
    ).sortedByDescending { it.xp }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "لائحة المتصدرين",
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // Gold League Header Shield
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        // Shield Badge (Trophy SVG replaced)
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(GildedAmber, Color(0xFFD97706))
                                    )
                                )
                                .border(BorderStroke(4.dp, Color(0xFFFDE047)), RoundedCornerShape(24.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.EmojiEvents,
                                contentDescription = "Trophy",
                                tint = Color(0xFFFDE047),
                                modifier = Modifier.size(68.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "المستوى الذهبي",
                            color = GildedAmber,
                            fontWeight = FontWeight.Black,
                            fontSize = 26.sp,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "قم بإنهاء درس لتنضم إلى لائحة المتصدرين لهذا الأسبوع والارتقاء نحو القمة!",
                            color = OnSurfaceVariantLight,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                        )

                        TactileButton(
                            onClick = onStartLesson,
                            backgroundColor = RoyalCobalt,
                            shadowColor = Color(0xFF003380),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "ابدأ درسًا الآن",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                // Competitors List Title
                item {
                    Text(
                        text = "ترتيب المتسابقين هذا الأسبوع:",
                        color = OnSurfaceLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Leaderboard items
                itemsIndexed(competitors) { index, competitor ->
                    val rank = index + 1
                    val isUser = competitor.isCurrentUser

                    val itemBgColor = if (isUser) RoyalCobalt.copy(alpha = 0.15f) else DarkSurface
                    val itemBorder = if (isUser) BorderStroke(2.dp, RoyalCobalt) else BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = itemBgColor),
                        border = itemBorder
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Rank indicator
                                Text(
                                    text = rank.toString(),
                                    color = when (rank) {
                                        1 -> GildedAmber
                                        2 -> Color(0xFF94A3B8) // Silver
                                        3 -> Color(0xFFB45309) // Bronze
                                        else -> OnSurfaceVariantLight
                                    },
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    modifier = Modifier.width(24.dp),
                                    textAlign = TextAlign.Center
                                )

                                // Avatar vector wrapper
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(competitor.avatarTint.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = competitor.avatarIcon,
                                        contentDescription = "Avatar",
                                        tint = competitor.avatarTint,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                // Username
                                Text(
                                    text = competitor.username,
                                    color = if (isUser) RoyalCobalt else OnSurfaceLight,
                                    fontWeight = if (isUser) FontWeight.Black else FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }

                            // XP indicator
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "${competitor.xp}",
                                    color = OnSurfaceLight,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "نقطة",
                                    color = OnSurfaceVariantLight,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
