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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.semaViewModel
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.theme.semaIcons
import com.example.ui.navigation.MainDestination
import kotlinx.coroutines.delay

@Composable
fun LessonScreen(
    viewModel: semaViewModel,
    onBackToHome: () -> Unit,
    onNavigate: (MainDestination) -> Unit = {}
) {
    val currentExercises by viewModel.currentExercises.collectAsState()
    val currentIndex by viewModel.currentExerciseIndex.collectAsState()
    val hearts by viewModel.heartsLocal.collectAsState()
    val userProgress by viewModel.userProgress.collectAsState()
    val gems = userProgress?.gems ?: 0

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

    var prevHearts by remember { mutableStateOf(hearts) }
    var showHeartLoss by remember { mutableStateOf(false) }
    LaunchedEffect(hearts) {
        if (hearts < prevHearts) {
            showHeartLoss = true
        }
        prevHearts = hearts
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
        ) {
            when {
                isFinished -> {
                    LessonCompletionOverlay(
                        xpGained = xpGained,
                        gemsGained = gemsGained,
                        lessonId = activeExercise?.lessonId ?: 1,
                        onFinishClick = onBackToHome,
                        onNavigate = onNavigate
                    )
                }

                hearts <= 0 -> {
                    OutOfHeartsOverlay(
                        gemsAvailable = gems,
                        onRechargeClick = { viewModel.rechargeHearts() },
                        onQuitClick = onBackToHome
                    )
                }

                activeExercise != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .navigationBarsPadding()
                    ) {
                        val progressFloat = (currentIndex.toFloat()) / currentExercises.size.coerceAtLeast(1).toFloat()
                        ExerciseProgressBar(
                            progress = progressFloat,
                            hearts = hearts,
                            gems = gems,
                            onCloseClick = onBackToHome,
                            showHeartLoss = showHeartLoss,
                            onHeartLossShown = { showHeartLoss = false }
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 24.dp, vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {
                            ExerciseCardRouter(
                                exercise = activeExercise,
                                viewModel = viewModel,
                                selectedOption = selectedOption,
                                selectedWords = selectedWords,
                                remainingWords = remainingWords,
                                textAnswer = textAnswer
                            )
                        }

                        Box(
                            modifier = Modifier.fillMaxWidth()
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
                                    color = SurfaceDark,
                                    tonalElevation = 8.dp,
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                                ) {
                                    Box(
                                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
                                    ) {
                                        semaButton(
                                            onClick = { viewModel.checkAnswer() },
                                            enabled = isInputProvided,
                                            backgroundColor = if (isInputProvided) PrimaryBlue else SurfaceVariant,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "تحقق",
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(color = PrimaryBlue)
                            Text(
                                text = "جاري تحميل التمارين...",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LessonCompletionOverlay(
    xpGained: Int,
    gemsGained: Int,
    lessonId: Int = 1,
    onFinishClick: () -> Unit,
    onNavigate: (MainDestination) -> Unit = {}
) {
    var showContent by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(200)
        showContent = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = showContent,
            enter = scaleIn(animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow)) + fadeIn()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                semaAvatar(
                    size = 100.dp,
                    backgroundColor = SuccessGreen.copy(alpha = 0.15f),
                    content = {
                        Icon(
                            imageVector = semaIcons.checkCircle(),
                            contentDescription = "Success",
                            tint = SuccessGreen,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "أحسنت!",
                    style = MaterialTheme.typography.displayMedium,
                    color = SuccessGreen,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "لقد أتممت الدرس بنجاح!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StaggeredEntrance(index = 0, staggerDelayMs = 300) {
                semaCard(modifier = Modifier.weight(1f)) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("نقاط خبرة", style = MaterialTheme.typography.labelLarge, color = XpYellow)
                        AnimatedCounter(
                            targetValue = xpGained,
                            color = TextPrimary,
                            textStyle = MaterialTheme.typography.headlineMedium
                        )
                        Icon(
                            imageVector = semaIcons.flashOn(),
                            contentDescription = "XP",
                            tint = XpYellow,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            StaggeredEntrance(index = 1, staggerDelayMs = 300) {
                semaCard(modifier = Modifier.weight(1f)) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("جواهر مكافأة", style = MaterialTheme.typography.labelLarge, color = PrimaryBlue)
                        AnimatedCounter(
                            targetValue = gemsGained,
                            color = TextPrimary,
                            textStyle = MaterialTheme.typography.headlineMedium
                        )
                        Icon(
                            imageVector = semaIcons.diamond(),
                            contentDescription = "Gems",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // Extra practice buttons
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(animationSpec = tween(600, delayMillis = 400))
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    semaButton(
                        onClick = { onNavigate(MainDestination.Pronunciation) },
                        modifier = Modifier.weight(1f),
                        backgroundColor = PrimaryBlue.copy(alpha = 0.8f)
                    ) {
                        Icon(semaIcons.flashOn(), contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("نطق", style = MaterialTheme.typography.labelMedium)
                    }
                    semaButton(
                        onClick = { onNavigate(MainDestination.Listening(lessonId)) },
                        modifier = Modifier.weight(1f),
                        backgroundColor = PrimaryBlue.copy(alpha = 0.8f)
                    ) {
                        Icon(semaIcons.translate(), contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("استماع", style = MaterialTheme.typography.labelMedium)
                    }
                }

                semaButton(
                    onClick = onFinishClick,
                    modifier = Modifier.fillMaxWidth(),
                    variant = semaButtonVariant.Outline
                ) {
                    Text(
                        text = "إنهاء",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
fun OutOfHeartsOverlay(
    gemsAvailable: Int,
    onRechargeClick: () -> Unit,
    onQuitClick: () -> Unit
) {
    var showContent by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(150)
        showContent = true
    }

    ShakeAnimation(shake = showContent) { shakeMod ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .then(shakeMod),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = showContent,
                enter = scaleIn() + fadeIn()
            ) {
                semaAvatar(
                    size = 100.dp,
                    backgroundColor = ErrorRed.copy(alpha = 0.15f),
                    content = {
                        Icon(
                            imageVector = semaIcons.favoriteBorder(),
                            contentDescription = "Out of Hearts",
                            tint = ErrorRed,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "نفدت القلوب!",
                style = MaterialTheme.typography.displayMedium,
                color = ErrorRed,
                textAlign = TextAlign.Center
            )

            Text(
                text = "لا تقلق! يمكنك إعادة شحن قلوبك مقابل 350 جوهرة.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )

            semaButton(
                onClick = onRechargeClick,
                enabled = gemsAvailable >= 350,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = semaIcons.diamond(),
                    contentDescription = "Gems",
                    tint = TextPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "إعادة التعبئة بـ 350 جوهرة",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            semaButton(
                onClick = onQuitClick,
                variant = semaButtonVariant.Outline,
                contentColor = TextSecondary
            ) {
                Text(
                    text = "العودة إلى الرئيسية",
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}
