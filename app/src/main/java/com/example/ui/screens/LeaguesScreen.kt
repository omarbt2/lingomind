package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.components.LoadingSkeleton

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
    competitors: List<Competitor>,
    onStartLesson: () -> Unit,
    onBack: () -> Unit,
    isLoading: Boolean = false
) {
    if (isLoading) {
        LoadingSkeleton()
    } else {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "لائحة المتصدرين",
                            style = MaterialTheme.typography.headlineSmall,
                            color = TextPrimary
                        )
                    },
                    navigationIcon = {
                        semaIconButton(
                            onClick = onBack,
                            content = {
                                Icon(
                                    imageVector = semaIcons.arrowBack(),
                                    contentDescription = "رجوع",
                                    tint = TextPrimary
                                )
                            }
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = BackgroundDark
                    )
                )
            },
            containerColor = BackgroundDark
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // League Header
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(MaterialTheme.shapes.large)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(XpYellow, Color(0xFFD97706))
                                    )
                                )
                                .border(3.dp, Color(0xFFFDE047), MaterialTheme.shapes.large),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = semaIcons.emojiEvents(),
                                contentDescription = "Trophy",
                                tint = Color(0xFFFDE047),
                                modifier = Modifier.size(56.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "المستوى الذهبي",
                            color = XpYellow,
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "أكمل درسًا لتنضم إلى لائحة المتصدرين!",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                        )

                        semaButton(onClick = onStartLesson) {
                            Text(
                                text = "ابدأ درسًا الآن",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }

                // Competitors title
                item {
                    Text(
                        text = "ترتيب المتسابقين:",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Competitor items
                if (competitors.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            EmptyState(
                                icon = semaIcons.emojiEvents(),
                                title = "لا يوجد متسابقون",
                                message = "ابدأ التعلم لتظهر في لوحة الصدارة"
                            )
                        }
                    }
                } else {
                    itemsIndexed(competitors) { index, competitor ->
                        val rank = index + 1
                        val isUser = competitor.isCurrentUser

                    StaggeredEntrance(index = index) {
                    semaCard(highlighted = isUser) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = rank.toString(),
                                    color = when (rank) {
                                        1 -> XpYellow
                                        2 -> Color(0xFF94A3B8)
                                        3 -> Color(0xFFB45309)
                                        else -> TextSecondary
                                    },
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.width(24.dp),
                                    textAlign = TextAlign.Center
                                )

                                semaAvatar(
                                    size = 40.dp,
                                    backgroundColor = competitor.avatarTint.copy(alpha = 0.2f),
                                    content = {
                                        Icon(
                                            imageVector = competitor.avatarIcon,
                                            contentDescription = "Avatar",
                                            tint = competitor.avatarTint,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                )

                                Text(
                                    text = competitor.username,
                                    color = if (isUser) PrimaryBlue else TextPrimary,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = if (isUser) FontWeight.Black else FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "${competitor.xp}",
                                    color = TextPrimary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "XP",
                                    color = TextSecondary,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                    }
                }
                }
            }
        }
    }
}
}
