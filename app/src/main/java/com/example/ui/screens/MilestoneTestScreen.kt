package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.data.MilestoneTestQuestion
import com.example.ui.semaViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun MilestoneTestScreen(
    viewModel: semaViewModel,
    onBackToHome: () -> Unit,
    onNavigateToResult: (phaseId: Int, score: Int, passed: Boolean) -> Unit = { _, _, _ -> }
) {
    val milestoneTest by viewModel.milestoneTest.collectAsState()
    val questions by viewModel.milestoneQuestions.collectAsState()
    val currentIndex by viewModel.milestoneQuestionIndex.collectAsState()
    val selectedOption by viewModel.milestoneSelectedOption.collectAsState()
    val answerChecked by viewModel.milestoneAnswerChecked.collectAsState()
    val score by viewModel.milestoneScore.collectAsState()
    val isFinished by viewModel.milestoneTestFinished.collectAsState()
    val isPassed by viewModel.milestoneTestPassed.collectAsState()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
        ) {
            when {
                isFinished -> {
                    LaunchedEffect(Unit) {
                        onNavigateToResult(milestoneTest?.phase_id ?: 1, score, isPassed)
                    }
                }

                questions.isNotEmpty() && currentIndex < questions.size -> {
                    val question = questions[currentIndex]
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                    ) {
                        ExerciseProgressBar(
                            progress = (currentIndex + 1).toFloat() / questions.size,
                            hearts = 5,
                            gems = 0,
                            onCloseClick = {
                                viewModel.resetMilestoneTest()
                                onBackToHome()
                            }
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = milestoneTest?.title_ar ?: "اختبار المرحلة",
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (milestoneTest?.description_ar != null) {
                                Text(
                                    text = milestoneTest!!.description_ar!!,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "السؤال ${currentIndex + 1} من ${questions.size}",
                                style = MaterialTheme.typography.labelLarge,
                                color = XpYellow,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            semaCard {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = question.question,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextPrimary
                                    )

                                    if (question.explanation != null) {
                                        Text(
                                            text = question.explanation,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }

                            question.options.forEachIndexed { index, option ->
                                val isSelected = selectedOption == index
                                val isCorrectOption = answerChecked && index == question.correctIndex
                                val isWrongOption = answerChecked && isSelected && !isCorrectOption

                                val bgColor = when {
                                    isCorrectOption -> SuccessGreen.copy(alpha = 0.2f)
                                    isWrongOption -> ErrorRed.copy(alpha = 0.2f)
                                    isSelected -> PrimaryBlue.copy(alpha = 0.15f)
                                    else -> SurfaceDark
                                }
                                val borderColor = when {
                                    isCorrectOption -> SuccessGreen
                                    isWrongOption -> ErrorRed
                                    isSelected -> PrimaryBlue
                                    else -> Outline
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(bgColor)
                                        .border(2.dp, borderColor, RoundedCornerShape(16.dp))
                                        .clickable(enabled = !answerChecked) {
                                            viewModel.selectMilestoneOption(index)
                                        }
                                        .padding(16.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isCorrectOption) SuccessGreen
                                                    else if (isWrongOption) ErrorRed
                                                    else if (isSelected) PrimaryBlue
                                                    else SurfaceVariant
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = if (answerChecked && isCorrectOption) "✓"
                                                       else if (answerChecked && isWrongOption) "✗"
                                                       else "${('A' + index)}",
                                                color = TextPrimary,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Text(
                                            text = option,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = TextPrimary
                                        )
                                    }
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            if (!answerChecked) {
                                semaButton(
                                    onClick = { viewModel.confirmMilestoneAnswer() },
                                    enabled = selectedOption != null,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("تأكيد الإجابة")
                                }
                            } else {
                                val isLast = currentIndex >= questions.size - 1
                                semaButton(
                                    onClick = {
                                        if (isLast) viewModel.submitMilestoneTest()
                                        else viewModel.nextMilestoneQuestion()
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(if (isLast) "إنهاء الاختبار" else "التالي")
                                }
                            }
                        }
                    }
                }

                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "جارٍ تحميل الاختبار...",
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MilestoneTestResult(
    score: Int,
    total: Int,
    passScore: Int,
    passed: Boolean,
    onFinishClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        if (passed) SuccessGreen.copy(alpha = 0.2f)
                        else ErrorRed.copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (passed) "✓" else "✗",
                    fontSize = 48.sp,
                    color = if (passed) SuccessGreen else ErrorRed
                )
            }

            Text(
                text = if (passed) "أحسنت!" else "لم تنجح هذه المرة",
                style = MaterialTheme.typography.headlineMedium,
                color = if (passed) SuccessGreen else ErrorRed
            )

            Text(
                text = "النتيجة: $score من $total",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )

            Text(
                text = "درجة النجاح: $passScore",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )

            if (passed) {
                Text(
                    text = "تم فتح المرحلة التالية! يمكنك الآن متابعة الدروس الجديدة.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "حاول مرة أخرى لفتح المحتوى التالي.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            semaButton(
                onClick = onFinishClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("العودة إلى الرئيسية")
            }
        }
    }
}
