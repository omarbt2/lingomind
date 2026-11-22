package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MilestoneTestEntity
import com.example.data.MilestoneAttemptEntity
import com.example.ui.semaViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun MilestoneResultScreen(
    viewModel: semaViewModel,
    phaseId: Int,
    score: Int,
    passed: Boolean,
    onRetry: () -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    val milestoneTest by viewModel.milestoneTest.collectAsState()
    val attempts by viewModel.milestoneAttempts.collectAsState()

    LaunchedEffect(phaseId) {
        viewModel.fetchMilestoneTest(phaseId)
        viewModel.fetchMilestoneAttempts(phaseId)
    }

    var showContent by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(200)
        showContent = true
    }

    val totalQuestions = milestoneTest?.let {
        try {
            val questions = com.squareup.moshi.Moshi.Builder().addLast(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory()).build()
                .adapter(com.example.data.MilestoneTestQuestion::class.java)
            it.questions?.let { q -> q.length / 50 } ?: 10
        } catch (e: Exception) { 10 }
    } ?: 10

    val percentage = if (totalQuestions > 0) (score.toFloat() / totalQuestions * 100).toInt() else 0

    // Animated score
    val animatedScore = remember { Animatable(0f) }
    LaunchedEffect(showContent) {
        if (showContent) {
            animatedScore.animateTo(
                targetValue = percentage.toFloat(),
                animationSpec = tween(1500, easing = FastOutSlowInEasing)
            )
        }
    }

    val scoreScale by animateFloatAsState(
        targetValue = if (showContent) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "score_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(if (passed) SuccessGreen.copy(alpha = 0.15f) else ErrorRed.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (passed) semaIcons.checkCircle() else semaIcons.close(),
                contentDescription = null,
                tint = if (passed) SuccessGreen else ErrorRed,
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        // Title
        Text(
            text = if (passed) "مبروك! اجتزت الاختبار!" else "لم تجتز الاختبار",
            style = MaterialTheme.typography.headlineSmall,
            color = if (passed) SuccessGreen else ErrorRed,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = milestoneTest?.title_ar ?: "اختبار المرحلة",
            color = TextSecondary,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.height(32.dp))

        // Score circle
        Box(
            modifier = Modifier.size(160.dp).clip(CircleShape).background(SurfaceDark),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${animatedScore.value.toInt()}%",
                    style = MaterialTheme.typography.displayLarge,
                    color = if (passed) SuccessGreen else ErrorRed,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.scale(scoreScale)
                )
                Text(
                    text = "$score / $totalQuestions",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Pass score indicator
        val passScore = milestoneTest?.pass_score ?: (totalQuestions * 60 / 100)
        Text(
            text = "الحد الأدنى للنجاح: $passScore / $totalQuestions",
            color = TextSecondary,
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(Modifier.height(32.dp))

        // Attempts history
        if (attempts.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "المحاولات السابقة",
                        color = TextPrimary,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    attempts.take(5).forEachIndexed { index, attempt ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "محاولة ${attempts.size - index}",
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "${attempt.score}/${totalQuestions}",
                                color = if (attempt.passed) SuccessGreen else ErrorRed,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Actions
        if (!passed) {
            semaButton(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("إعادة المحاولة", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(12.dp))
        }

        semaButton(
            onClick = if (passed) onContinue else onBack,
            variant = if (passed) semaButtonVariant.Primary else semaButtonVariant.Outline,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (passed) "المرحلة التالية" else "العودة للخريطة",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}