package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import com.example.ui.components.HapticHelper
import kotlinx.coroutines.delay
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExerciseEntity
import com.example.ui.semaViewModel
import com.example.ui.theme.*
import com.example.ui.theme.semaIcons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.VolumeUp

// --- Top Progress Bar ---
@Composable
fun ExerciseProgressBar(
    progress: Float,
    hearts: Int,
    gems: Int,
    onCloseClick: () -> Unit,
    showHeartLoss: Boolean = false,
    onHeartLossShown: () -> Unit = {}
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "progress"
    )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            semaIconButton(
                onClick = onCloseClick,
                content = {
                    Icon(
                        imageVector = semaIcons.close(),
                        contentDescription = "إغلاق الدرس",
                        tint = TextPrimary
                    )
                }
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(18.dp)
                    .padding(horizontal = 12.dp)
                    .clip(CircleShape)
                    .background(SurfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .clip(CircleShape)
                        .background(SuccessGreen)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .background(Color.White.copy(alpha = 0.2f))
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = semaIcons.favorite(),
                            contentDescription = "القلوب",
                            tint = ErrorRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = hearts.toString(),
                            color = ErrorRed,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    if (showHeartLoss) {
                        FloatUpText(
                            text = "-1",
                            color = ErrorRed,
                            modifier = Modifier.align(Alignment.TopStart).offset(x = (-8).dp, y = (-4).dp)
                        )
                        LaunchedEffect(Unit) {
                            delay(800)
                            onHeartLossShown()
                        }
                    }
                }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = semaIcons.diamond(),
                    contentDescription = "الجواهر",
                    tint = PrimaryBlue,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = gems.toString(),
                    color = PrimaryBlue,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            }
        }
}

// --- Bottom Feedback Sheet ---
@Composable
fun ExerciseFeedbackBar(
    isCorrect: Boolean,
    isFreeWriting: Boolean,
    correctAnswer: String?,
    explanationAr: String,
    onContinueClick: () -> Unit
) {
    val context = LocalContext.current
    val barColor = when {
        isFreeWriting -> SurfaceVariant
        isCorrect -> SuccessGreen.copy(alpha = 0.15f)
        else -> ErrorRed.copy(alpha = 0.15f)
    }

    val titleText = when {
        isFreeWriting -> "تم إرسال تمرينك الحُر!"
        isCorrect -> "أحسنت!"
        else -> "إجابة خاطئة!"
    }

    val titleColor = when {
        isFreeWriting -> PrimaryBlue
        isCorrect -> SuccessGreen
        else -> ErrorRed
    }

    val buttonBg = when {
        isFreeWriting -> PrimaryBlue
        isCorrect -> SuccessGreen
        else -> ErrorRed
    }

    // Trigger haptic feedback when feedback bar appears
    LaunchedEffect(isCorrect) {
        if (isCorrect) {
            HapticHelper.performHapticSuccess(context)
        } else if (!isFreeWriting) {
            HapticHelper.performHapticError(context)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(barColor)
            .border(BorderStroke(2.dp, Color.White.copy(alpha = 0.05f)))
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when {
isCorrect -> semaIcons.check()
else -> semaIcons.close()
                                },
                                contentDescription = null,
                                tint = titleColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                Column {
                    Text(
                        text = titleText,
                        color = titleColor,
                        style = MaterialTheme.typography.headlineSmall
                    )

                    if (!isCorrect && !isFreeWriting && correctAnswer != null) {
                        Text(
                            text = "الإجابة الصحيحة: $correctAnswer",
                            color = TextPrimary,
                            modifier = Modifier.padding(top = 4.dp),
                            style = TextStyle(textDirection = TextDirection.Ltr, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            if (explanationAr.isNotEmpty()) {
                Text(
                    text = explanationAr,
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            semaButton(
                onClick = onContinueClick,
                backgroundColor = buttonBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "متابعة",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

// --- MCQ view ---
@Composable
fun McqExerciseView(
    prompt: String,
    options: List<String>,
    selectedOption: Int?,
    onOptionSelected: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = prompt,
            color = TextPrimary,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        options.forEachIndexed { index, option ->
            semaCardOption(
                selected = (selectedOption == index),
                onClick = { onOptionSelected(index) }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(if (selectedOption == index) PrimaryBlue else SurfaceVariant)
                            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)), MaterialTheme.shapes.small),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (index + 1).toString(),
                            color = TextPrimary,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    Text(
                        text = option,
                        color = TextPrimary,
                        style = TextStyle(textDirection = TextDirection.Ltr, fontSize = MaterialTheme.typography.titleMedium.fontSize),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

// --- Fill Blank view ---
@Composable
fun FillBlankExerciseView(
    prompt: String,
    sentence: String,
    wordBank: List<String>,
    selectedWords: List<String>,
    onChipSelected: (String) -> Unit,
    onChipDeselected: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = prompt,
            color = TextPrimary,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        val parts = sentence.split("___")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (parts.isNotEmpty()) {
                Text(
                    text = parts[0],
                    color = TextPrimary,
                    style = TextStyle(textDirection = TextDirection.Ltr, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                )

                if (selectedWords.isNotEmpty()) {
                    semaChip(
                        text = selectedWords[0],
                        selected = true,
                        onClick = { onChipDeselected(selectedWords[0]) }
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .width(80.dp)
                            .height(36.dp)
                            .border(2.dp, Color.White.copy(alpha = 0.3f), MaterialTheme.shapes.small)
                    )
                }

                if (parts.size > 1) {
                    Text(
                        text = parts[1],
                        color = TextPrimary,
                        style = TextStyle(textDirection = TextDirection.Ltr, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "مخزن الكلمات:",
            color = TextSecondary,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        FlowRowLayout(
            horizontalGap = 8.dp,
            verticalGap = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            wordBank.forEach { word ->
                semaChip(
                    text = word,
                    selected = false,
                    onClick = { onChipSelected(word) }
                )
            }
        }
    }
}

// --- Reorder Exercise view ---
@Composable
fun ReorderExerciseView(
    prompt: String,
    remainingWords: List<String>,
    selectedWords: List<String>,
    onChipSelected: (String) -> Unit,
    onChipDeselected: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = prompt,
            color = TextPrimary,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(SurfaceDark)
                .border(BorderStroke(2.dp, Color.White.copy(alpha = 0.08f)), MaterialTheme.shapes.medium)
                .padding(16.dp),
            contentAlignment = Alignment.TopStart
        ) {
            if (selectedWords.isEmpty()) {
                Text(
                    text = "اضغط على الكلمات بالأسفل لترتيبها هنا...",
                    color = TextSecondary.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                FlowRowLayout(horizontalGap = 8.dp, verticalGap = 8.dp) {
                    selectedWords.forEach { word ->
                        semaChip(
                            text = word,
                            selected = true,
                            onClick = { onChipDeselected(word) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        FlowRowLayout(
            horizontalGap = 8.dp,
            verticalGap = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            remainingWords.forEach { word ->
                semaChip(
                    text = word,
                    selected = false,
                    onClick = { onChipSelected(word) }
                )
            }
        }
    }
}

// --- Sentence Transformation view ---
@Composable
fun TransformExerciseView(
    prompt: String,
    originalSentence: String,
    instructionAr: String,
    textAnswer: String,
    onAnswerChanged: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = prompt,
            color = TextPrimary,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        semaCard {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "الجملة الأصلية:",
                    color = PrimaryBlue,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = originalSentence,
                    color = TextPrimary,
                    style = TextStyle(textDirection = TextDirection.Ltr, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                )
            }
        }

        Text(
            text = "التوجيه: $instructionAr",
            color = XpYellow,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = textAnswer,
            onValueChange = onAnswerChanged,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                textDirection = TextDirection.Ltr,
                color = TextPrimary,
                fontSize = 16.sp
            ),
            placeholder = { Text("اكتب الإجابة بالإنجليزية هنا...") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                focusedContainerColor = SurfaceDark,
                unfocusedContainerColor = SurfaceDark
            ),
            shape = MaterialTheme.shapes.medium,
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )
    }
}

// --- Free Writing Exercise view ---
@Composable
fun FreeWritingExerciseView(
    prompt: String,
    topicAr: String,
    minWords: Int,
    textAnswer: String,
    onAnswerChanged: (String) -> Unit
) {
    val wordCount = remember(textAnswer) {
        textAnswer.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }.size
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = prompt,
            color = TextPrimary,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        semaCard {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "الموضوع المطلوب:",
                    color = XpYellow,
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = topicAr,
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "الحد الأدنى: $minWords كلمة",
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "العدد الحالي: $wordCount كلمة",
                color = if (wordCount >= minWords) SuccessGreen else ErrorRed,
                style = MaterialTheme.typography.labelLarge
            )
        }

        OutlinedTextField(
            value = textAnswer,
            onValueChange = onAnswerChanged,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            textStyle = TextStyle(
                textDirection = TextDirection.Ltr,
                color = TextPrimary,
                fontSize = 16.sp
            ),
            placeholder = { Text("اكتب موضوعك بالإنجليزية هنا بالتفصيل...") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                focusedContainerColor = SurfaceDark,
                unfocusedContainerColor = SurfaceDark
            ),
            shape = MaterialTheme.shapes.medium
        )
    }
}

// --- Tactile Card Option ---
@Composable
fun semaCardOption(
    selected: Boolean,
    onClick: () -> Unit,
    isCorrect: Boolean? = null,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "option_press"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isCorrect == true -> SuccessGreen
            isCorrect == false -> ErrorRed
            selected -> PrimaryBlue
            else -> Color.White.copy(alpha = 0.1f)
        },
        animationSpec = tween(300),
        label = "option_border"
    )

    val bgColor by animateColorAsState(
        targetValue = when {
            isCorrect == true -> SuccessGreen.copy(alpha = 0.15f)
            isCorrect == false -> ErrorRed.copy(alpha = 0.15f)
            selected -> PrimaryBlue.copy(alpha = 0.15f)
            else -> SurfaceDark
        },
        animationSpec = tween(300),
        label = "option_bg"
    )

    val borderThickness = if (selected || isCorrect != null) 3.dp else 2.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = pressScale; scaleY = pressScale }
            .clip(MaterialTheme.shapes.medium)
            .background(bgColor)
            .border(BorderStroke(borderThickness, borderColor), MaterialTheme.shapes.medium)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(16.dp)
    ) {
        content()
    }
}

// --- Listening Exercise View ---
@Composable
fun ListeningExerciseView(
    prompt: String,
    audioUrl: String,
    transcript: String,
    question: String,
    options: List<String>,
    selectedOption: Int?,
    onOptionSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = prompt,
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary
        )

        // Audio player card
        semaCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.VolumeUp,
                    contentDescription = "تشغيل الصوت",
                    tint = PrimaryBlue,
                    modifier = Modifier.size(48.dp)
                )

                // Play button
                var isPlaying by remember { mutableStateOf(false) }
                semaButton(
                    onClick = { isPlaying = !isPlaying },
                    backgroundColor = PrimaryBlue
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) "إيقاف" else "تشغيل",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isPlaying) "إيقاف" else "تشغيل التسجيل")
                }
            }
        }

        // Question
        Text(
            text = question,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )

        // MCQ options
        options.forEachIndexed { index, option ->
            val isSelected = selectedOption == index
            val bgColor = if (isSelected) PrimaryBlue.copy(alpha = 0.15f) else SurfaceDark
            val borderColor = if (isSelected) PrimaryBlue else Outline

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(bgColor)
                    .border(2.dp, borderColor, RoundedCornerShape(16.dp))
                    .clickable { onOptionSelected(index) }
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) PrimaryBlue else SurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${('A' + index)}",
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
}

// --- Main polymorphic Exercise Card router ---
@Composable
fun ExerciseCardRouter(
    exercise: ExerciseEntity,
    viewModel: semaViewModel,
    selectedOption: Int?,
    selectedWords: List<String>,
    remainingWords: List<String>,
    textAnswer: String
) {
    val activeMcq by viewModel.activeMcqContent.collectAsState()
    val activeFillBlank by viewModel.activeFillBlankContent.collectAsState()
    val activeReorder by viewModel.activeReorderContent.collectAsState()
    val activeTransform by viewModel.activeTransformContent.collectAsState()
    val activeFreeWriting by viewModel.activeFreeWritingContent.collectAsState()
    val activeListening by viewModel.activeListeningContent.collectAsState()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        when (exercise.exerciseType) {
            "mcq" -> {
                activeMcq?.let { mcq ->
                    McqExerciseView(
                        prompt = exercise.promptAr,
                        options = mcq.options,
                        selectedOption = selectedOption,
                        onOptionSelected = { viewModel.selectOption(it) }
                    )
                } ?: Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
            "fill_blank" -> {
                activeFillBlank?.let { blank ->
                    FillBlankExerciseView(
                        prompt = exercise.promptAr,
                        sentence = blank.sentence,
                        wordBank = remainingWords,
                        selectedWords = selectedWords,
                        onChipSelected = { viewModel.selectWordChip(it) },
                        onChipDeselected = { viewModel.deselectWordChip(it) }
                    )
                } ?: Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
            "reorder" -> {
                activeReorder?.let { reorder ->
                    ReorderExerciseView(
                        prompt = exercise.promptAr,
                        remainingWords = remainingWords,
                        selectedWords = selectedWords,
                        onChipSelected = { viewModel.selectWordChip(it) },
                        onChipDeselected = { viewModel.deselectWordChip(it) }
                    )
                } ?: Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
            "transform" -> {
                activeTransform?.let { transform ->
                    TransformExerciseView(
                        prompt = exercise.promptAr,
                        originalSentence = transform.original_sentence,
                        instructionAr = transform.instruction_ar,
                        textAnswer = textAnswer,
                        onAnswerChanged = { viewModel.updateTextAnswer(it) }
                    )
                } ?: Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
            "free_writing" -> {
                activeFreeWriting?.let { free ->
                    FreeWritingExerciseView(
                        prompt = exercise.promptAr,
                        topicAr = free.topic_ar,
                        minWords = free.min_words,
                        textAnswer = textAnswer,
                        onAnswerChanged = { viewModel.updateTextAnswer(it) }
                    )
                } ?: Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
            "listening" -> {
                activeListening?.let { listening ->
                    ListeningExerciseView(
                        prompt = exercise.promptAr,
                        audioUrl = listening.audio_url,
                        transcript = listening.transcript,
                        question = listening.question,
                        options = listening.options,
                        selectedOption = selectedOption,
                        onOptionSelected = { viewModel.selectOption(it) }
                    )
                } ?: Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
        }
    }
}

// --- FlowRow Layout ---
@Composable
fun FlowRowLayout(
    horizontalGap: androidx.compose.ui.unit.Dp = 8.dp,
    verticalGap: androidx.compose.ui.unit.Dp = 8.dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.layout.Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }
        val layoutWidth = constraints.maxWidth

        val rows = mutableListOf<List<androidx.compose.ui.layout.Placeable>>()
        var currentRow = mutableListOf<androidx.compose.ui.layout.Placeable>()
        var currentRowWidth = 0

        placeables.forEach { placeable ->
            val spacing = if (currentRow.isEmpty()) 0 else horizontalGap.roundToPx()
            if (currentRowWidth + spacing + placeable.width <= layoutWidth) {
                currentRow.add(placeable)
                currentRowWidth += spacing + placeable.width
            } else {
                rows.add(currentRow)
                currentRow = mutableListOf(placeable)
                currentRowWidth = placeable.width
            }
        }
        if (currentRow.isNotEmpty()) {
            rows.add(currentRow)
        }

        var totalHeight = 0
        rows.forEachIndexed { index, row ->
            val rowHeight = row.maxOfOrNull { it.height } ?: 0
            val spacing = if (index == 0) 0 else verticalGap.roundToPx()
            totalHeight += spacing + rowHeight
        }

        layout(layoutWidth, totalHeight) {
            var y = 0
            rows.forEachIndexed { index, row ->
                val rowHeight = row.maxOfOrNull { it.height } ?: 0
                val verticalSpacing = if (index == 0) 0 else verticalGap.roundToPx()
                y += verticalSpacing

                var x = 0
                row.forEachIndexed { wordIndex, placeable ->
                    val horizontalSpacing = if (wordIndex == 0) 0 else horizontalGap.roundToPx()
                    x += horizontalSpacing
                    placeable.placeRelative(x, y)
                    x += placeable.width
                }
                y += rowHeight
            }
        }
    }
}

// --- Keep TactileButton as alias for backward compatibility ---
@Composable
fun TactileButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = PrimaryBlue,
    shadowColor: Color = PrimaryBlue.copy(alpha = 0.6f),
    contentColor: Color = TextPrimary,
    content: @Composable RowScope.() -> Unit
) {
    semaButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        content = content
    )
}
