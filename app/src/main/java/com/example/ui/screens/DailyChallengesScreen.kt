package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DailyChallengeEntity
import com.example.data.UserDailyChallengeEntity
import com.example.ui.semaViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun DailyChallengesScreen(
    viewModel: semaViewModel,
    onBack: () -> Unit
) {
    val allChallenges by viewModel.allDailyChallenges.collectAsState()
    val userChallenges by viewModel.allUserDailyChallenges.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchAllDailyChallenges()
        viewModel.fetchAllUserDailyChallenges()
    }

    val challengeMap = remember(userChallenges) {
        userChallenges.associateBy { it.challengeId }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            semaIconButton(onClick = onBack) {
                Icon(semaIcons.arrowBack(), contentDescription = "رجوع", tint = TextPrimary)
            }
            Text(
                text = "التحديات اليومية",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            Box(modifier = Modifier.size(48.dp))
        }

        HorizontalDivider(color = SurfaceDark, thickness = 1.dp)

        if (allChallenges.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = semaIcons.emojiEvents(),
                        contentDescription = null,
                        tint = PrimaryBlue.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "لا توجد تحديات حالياً",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "عد لاحقاً لرؤية التحديات الجديدة",
                        color = TextSecondary.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(allChallenges) { challenge ->
                    val userChallenge = challengeMap[challenge.id]
                    val isCompleted = userChallenge?.completed == true
                    val score = userChallenge?.score

                    ChallengeCard(
                        challenge = challenge,
                        isCompleted = isCompleted,
                        score = score,
                        onComplete = {
                            viewModel.completeDailyChallenge(
                                score = (50..100).random()
                            )
                            viewModel.fetchAllUserDailyChallenges()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ChallengeCard(
    challenge: DailyChallengeEntity,
    isCompleted: Boolean,
    score: Int?,
    onComplete: () -> Unit
) {
    val borderColor = if (isCompleted) SuccessGreen else SurfaceVariant

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = if (isCompleted) CardDefaults.outlinedCardBorder().copy(
            width = 2.dp,
            brush = androidx.compose.ui.graphics.SolidColor(SuccessGreen.copy(alpha = 0.5f))
        ) else null
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isCompleted) SuccessGreen.copy(alpha = 0.2f) else PrimaryBlue.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = semaIcons.emojiEvents(),
                            contentDescription = null,
                            tint = if (isCompleted) SuccessGreen else PrimaryBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = challenge.titleAr,
                            color = TextPrimary,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = challenge.date,
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // XP reward badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = XpYellow.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = semaIcons.flashOn(),
                            contentDescription = null,
                            tint = XpYellow,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${challenge.xpReward} XP",
                            color = XpYellow,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Description
            Text(
                text = challenge.descriptionAr,
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall
            )

            // Progress bar
            if (isCompleted && score != null) {
                LinearProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = SuccessGreen,
                    trackColor = SurfaceVariant
                )
                Text(
                    text = "تم الإكمال! النتيجة: ${score}%",
                    color = SuccessGreen,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            } else if (!isCompleted) {
                LinearProgressIndicator(
                    progress = { 0f },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = PrimaryBlue,
                    trackColor = SurfaceVariant
                )
                semaButton(
                    onClick = onComplete,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("إكمال التحدي", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}