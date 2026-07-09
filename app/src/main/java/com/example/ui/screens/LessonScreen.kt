package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.ui.LingoMindViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun LessonScreen(
    viewModel: LingoMindViewModel,
    onBackToHome: () -> Unit
) {
    val currentExercises by viewModel.currentExercises.collectAsState()
    val currentIndex by viewModel.currentExerciseIndex.collectAsState()
    val hearts by viewModel.heartsLocal.collectAsState()
    val userProgress by viewModel.userProgress.collectAsState()
    val gems = userProgress?.gems ?: 1264

    val isAnswerChecked by viewModel.isAnswerChecked.collectAsState()
    val isAnswerCorrect by viewModel.isAnswerCorrect.collectAsState()
    val showFeedbackBar by viewModel.showFeedbackBar.collectAsState()

    val selectedOption by viewModel.selectedOptionIndex.collectAsState()
    val selectedWords by viewModel.selectedWordChips.collectAsState()
    val remainingWords by viewModel.remainingWordBank.collectAsState()
    val textAnswer by viewModel.textAnswer.collectAsState()

    val xpGained by viewModel.xpGainedInSession.collectAsState()
    val gemsGained by viewModel.gemsGainedInSession.collectAsState()
    val isFinished by viewModel.isLessonFinished.collectAsState()

    val activeExercise = currentExercises.getOrNull(currentIndex)

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
        ) {
            when {
                // Lesson completed successfully
                isFinished -> {
                    LessonCompletionOverlay(
                        xpGained = xpGained,
                        gemsGained = gemsGained,
                        onFinishClick = onBackToHome
                    )
                }

                // Out of hearts (failed)
                hearts <= 0 -> {
                    OutOfHeartsOverlay(
                        gemsAvailable = gems,
                        onRechargeClick = { viewModel.rechargeHearts() },
                        onQuitClick = onBackToHome
                    )
                }

                // Normal active lesson player
                activeExercise != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .navigationBarsPadding()
                    ) {
                        // Top Progress Bar
                        val progressFloat = (currentIndex.toFloat()) / currentExercises.size.coerceAtLeast(1).toFloat()
                        ExerciseProgressBar(
                            progress = progressFloat,
                            hearts = hearts,
                            gems = gems,
                            onCloseClick = onBackToHome
                        )

                        // Main Question viewport
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 24.dp, vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {
                            // Render correct prompt layout based on polymorphic card
                            ExerciseCardRouter(
                                exercise = activeExercise,
                                viewModel = viewModel,
                                selectedOption = selectedOption,
                                selectedWords = selectedWords,
                                remainingWords = remainingWords,
                                textAnswer = textAnswer
                            )
                        }

                        // Bottom Actions Row (Checks or Feedback sheet)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            if (showFeedbackBar) {
                                val isFreeWriting = activeExercise.exerciseType == "free_writing"
                                ExerciseFeedbackBar(
                                    isCorrect = isAnswerCorrect,
                                    isFreeWriting = isFreeWriting,
                                    correctAnswer = activeExercise.correctAnswer,
                                    explanationAr = activeExercise.explanationAr,
                                    onContinueClick = { viewModel.continueLesson() }
                                )
                            } else {
                                // Dynamic Check button state
                                val isInputProvided = when (activeExercise.exerciseType) {
                                    "mcq" -> selectedOption != null
                                    "fill_blank" -> selectedWords.isNotEmpty()
                                    "reorder" -> selectedWords.isNotEmpty()
                                    "transform" -> textAnswer.isNotBlank()
                                    "free_writing" -> textAnswer.isNotBlank()
                                    else -> false
                                }

                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = DarkSurface,
                                    tonalElevation = 8.dp,
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 24.dp, vertical = 20.dp)
                                    ) {
                                        TactileButton(
                                            onClick = { viewModel.checkAnswer() },
                                            enabled = isInputProvided,
                                            backgroundColor = if (isInputProvided) RoyalCobalt else Color(0xFF1E3A63),
                                            shadowColor = if (isInputProvided) Color(0xFF003380) else Color(0xFF0B1930),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "تحقق",
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 16.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Loading exercises state
                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(color = RoyalCobalt)
                            Text(
                                text = "جاري تحميل بطاقات تمارين LingoMind...",
                                color = OnSurfaceLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- Completion Screen overlay ---
@Composable
fun LessonCompletionOverlay(
    xpGained: Int,
    gemsGained: Int,
    onFinishClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(RoyalCobalt.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Success",
                tint = DuolingoGreen,
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "أحسنت صنعًا!",
            color = RoyalCobalt,
            fontWeight = FontWeight.Black,
            fontSize = 32.sp,
            textAlign = TextAlign.Center
        )

        Text(
            text = "لقد أتممت الدرس بنجاح واكتسبت معلومات ومهارات جديدة!",
            color = OnSurfaceLight,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
        )

        // Statistics Cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // XP Card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurfaceHigh)
                    .border(BorderStroke(2.dp, GildedAmber), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("نقاط خبرة", color = GildedAmber, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("+$xpGained", color = OnSurfaceLight, fontWeight = FontWeight.Black, fontSize = 24.sp)
                    Icon(
                        imageVector = Icons.Filled.Whatshot,
                        contentDescription = "XP",
                        tint = GildedAmber,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Gems Card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurfaceHigh)
                    .border(BorderStroke(2.dp, DiamondBlue), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("جواهر مكافأة", color = DiamondBlue, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("+$gemsGained", color = OnSurfaceLight, fontWeight = FontWeight.Black, fontSize = 24.sp)
                    Icon(
                        imageVector = Icons.Filled.Diamond,
                        contentDescription = "Gems",
                        tint = DiamondBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        TactileButton(
            onClick = onFinishClick,
            backgroundColor = RoyalCobalt,
            shadowColor = Color(0xFF003380),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "إنهاء",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp
            )
        }
    }
}

// --- Out of Hearts Screen overlay ---
@Composable
fun OutOfHeartsOverlay(
    gemsAvailable: Int,
    onRechargeClick: () -> Unit,
    onQuitClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(DuolingoRed.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.FavoriteBorder,
                contentDescription = "Out of Hearts",
                tint = DuolingoRed,
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "نفدت القلوب!",
            color = DuolingoRed,
            fontWeight = FontWeight.Black,
            fontSize = 32.sp,
            textAlign = TextAlign.Center
        )

        Text(
            text = "لا تقلق، فالخطأ جزء من التعلم! يمكنك إعادة شحن قلوبك بالكامل مقابل 350 جوهرة لمواصلة الدرس.",
            color = OnSurfaceLight,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
        )

        // Purchase Button
        TactileButton(
            onClick = onRechargeClick,
            enabled = gemsAvailable >= 350,
            backgroundColor = RoyalCobalt,
            shadowColor = Color(0xFF003380),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Filled.Diamond,
                contentDescription = "Gems",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "إعادة التعبئة بـ 350 جوهرة",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Return button
        TextButton(onClick = onQuitClick) {
            Text(
                text = "العودة إلى الرئيسية",
                color = OnSurfaceVariantLight,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}
