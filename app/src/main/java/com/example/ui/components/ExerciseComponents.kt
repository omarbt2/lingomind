package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExerciseEntity
import com.example.ui.LingoMindViewModel
import com.example.ui.theme.*

// --- Tactile 3D Button ---
@Composable
fun TactileButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = DuolingoGreen,
    shadowColor: Color = Color(0xFF3B8A01),
    contentColor: Color = Color.White,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    var isPressed by remember { mutableStateOf(false) }

    val offsetVal by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 0.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "press_offset"
    )

    Box(
        modifier = modifier
            .padding(bottom = 6.dp)
            .clickable(
                enabled = enabled,
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null
            )
    ) {
        // Shadow Layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = 6.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (enabled) shadowColor else Color(0xFF474E3E))
        )

        // Main Button Layer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = offsetVal)
                .clip(RoundedCornerShape(14.dp))
                .background(if (enabled) backgroundColor else Color(0xFF31372A))
                .border(
                    BorderStroke(2.dp, if (enabled) Color.White.copy(alpha = 0.15f) else Color.Transparent),
                    RoundedCornerShape(14.dp)
                )
                .padding(vertical = 14.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompositionLocalProvider(
                LocalContentColor provides if (enabled) contentColor else Color(0xFF8B947C)
            ) {
                content()
            }
        }
    }
}

// --- Tactile Card Option ---
@Composable
fun TactileCardOption(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedColor: Color = ElectricIndigo,
    content: @Composable BoxScope.() -> Unit
) {
    val borderThickness = if (selected) 3.dp else 2.dp
    val borderColor = if (selected) selectedColor else Color(0xFF414A36)
    val cardBackground = if (selected) selectedColor.copy(alpha = 0.15f) else DarkSurface

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardBackground)
            .border(BorderStroke(borderThickness, borderColor), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        content()
    }
}

// --- Top Progress Bar ---
@Composable
fun ExerciseProgressBar(
    progress: Float, // 0.0f to 1.0f
    hearts: Int,
    gems: Int,
    onCloseClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Close Button
        IconButton(onClick = onCloseClick) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "إغلاق الدرس",
                tint = OnSurfaceLight
            )
        }

        // Progress Bar Track
        Box(
            modifier = Modifier
                .weight(1f)
                .height(18.dp)
                .padding(horizontal = 12.dp)
                .clip(CircleShape)
                .background(DarkSurfaceHighest)
        ) {
            // Actual Progress Fill
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .clip(CircleShape)
                    .background(DuolingoGreen)
            ) {
                // Shiny highlight line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(Color.White.copy(alpha = 0.2f))
                )
            }
        }

        // Stats Display
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Hearts
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = hearts.toString(),
                    color = DuolingoRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "القلوب",
                    tint = DuolingoRed,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Gems
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = gems.toString(),
                    color = DiamondBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "💎",
                    fontSize = 16.sp
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
    val barColor = when {
        isFreeWriting -> DarkSurfaceHigh
        isCorrect -> DuolingoGreenDark
        else -> DuolingoRedDark
    }

    val iconEmoji = when {
        isFreeWriting -> "✍️"
        isCorrect -> listOf("🎉", "🔥", "✨", "🎯", "🙌").random()
        else -> "❌"
    }

    val titleText = when {
        isFreeWriting -> "تم إرسال تمرينك الحُر!"
        isCorrect -> listOf("ممتاز!", "رائع!", "إجابة صحيحة!", "🔥 عبقري!", "مذهل!").random()
        else -> "إجابة خاطئة!"
    }

    val titleColor = when {
        isFreeWriting -> DiamondBlue
        isCorrect -> DuolingoGreen
        else -> DuolingoRed
    }

    val buttonBg = when {
        isFreeWriting -> DiamondBlue
        isCorrect -> DuolingoGreen
        else -> DuolingoRed
    }

    val buttonShadow = when {
        isFreeWriting -> Color(0xFF0C82B5)
        isCorrect -> Color(0xFF3B8A01)
        else -> Color(0xFF9E1B1B)
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
                // Feedback icon wrapper
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = iconEmoji, fontSize = 24.sp)
                }

                Column {
                    Text(
                        text = titleText,
                        color = titleColor,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp
                    )

                    if (!isCorrect && !isFreeWriting && correctAnswer != null) {
                        Text(
                            text = "الإجابة الصحيحة: $correctAnswer",
                            color = OnSurfaceLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(top = 4.dp),
                            style = TextStyle(textDirection = TextDirection.Ltr)
                        )
                    }
                }
            }

            // Arabic explanation text
            if (explanationAr.isNotEmpty()) {
                Text(
                    text = explanationAr,
                    color = OnSurfaceVariantLight,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Button
            TactileButton(
                onClick = onContinueClick,
                backgroundColor = buttonBg,
                shadowColor = buttonShadow,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "متابعة",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
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
        // Arabic prompt
        Text(
            text = prompt,
            color = OnSurfaceLight,
            fontWeight = FontWeight.Black,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        options.forEachIndexed { index, option ->
            TactileCardOption(
                selected = (selectedOption == index),
                onClick = { onOptionSelected(index) }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Option index number indicator
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedOption == index) ElectricIndigo else DarkSurfaceHighest)
                            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (index + 1).toString(),
                            color = OnSurfaceLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    // Option Text
                    Text(
                        text = option,
                        color = OnSurfaceLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.End,
                        style = TextStyle(textDirection = TextDirection.Ltr)
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
    sentence: String, // e.g. "She ___ to market."
    wordBank: List<String>,
    selectedWords: List<String>,
    onChipSelected: (String) -> Unit,
    onChipDeselected: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Arabic Prompt
        Text(
            text = prompt,
            color = OnSurfaceLight,
            fontWeight = FontWeight.Black,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        // Sentence with placeholder or filled answer
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
                    color = OnSurfaceLight,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    style = TextStyle(textDirection = TextDirection.Ltr)
                )

                // The blank area
                if (selectedWords.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ElectricIndigo.copy(alpha = 0.2f))
                            .border(BorderStroke(2.dp, ElectricIndigo), RoundedCornerShape(12.dp))
                            .clickable { onChipDeselected(selectedWords[0]) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = selectedWords[0],
                            color = OnSurfaceLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            style = TextStyle(textDirection = TextDirection.Ltr)
                        )
                    }
                } else {
                    // Empty blank indicator
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .width(80.dp)
                            .height(36.dp)
                            .background(Color.Transparent)
                            .border(BorderStroke(2.dp, Color.White.copy(alpha = 0.3f)), RoundedCornerShape(8.dp))
                    )
                }

                if (parts.size > 1) {
                    Text(
                        text = parts[1],
                        color = OnSurfaceLight,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        style = TextStyle(textDirection = TextDirection.Ltr)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Word Bank Chips Pool
        Text(
            text = "مخزن الكلمات:",
            color = OnSurfaceVariantLight,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        FlowRowLayout(
            horizontalGap = 8.dp,
            verticalGap = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            wordBank.forEach { word ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurfaceHigh)
                        .border(BorderStroke(2.dp, Color.White.copy(alpha = 0.1f)), RoundedCornerShape(12.dp))
                        .clickable { onChipSelected(word) }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = word,
                        color = OnSurfaceLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        style = TextStyle(textDirection = TextDirection.Ltr)
                    )
                }
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
        // Arabic prompt
        Text(
            text = prompt,
            color = OnSurfaceLight,
            fontWeight = FontWeight.Black,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        // Sentence assembly zone
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurface)
                .border(BorderStroke(2.dp, Color.White.copy(alpha = 0.08f)), RoundedCornerShape(16.dp))
                .padding(16.dp),
            contentAlignment = Alignment.TopStart
        ) {
            if (selectedWords.isEmpty()) {
                Text(
                    text = "اضغط على الكلمات بالأسفل لترتيبها هنا...",
                    color = OnSurfaceVariantLight.copy(alpha = 0.5f),
                    fontSize = 15.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                FlowRowLayout(horizontalGap = 8.dp, verticalGap = 8.dp) {
                    selectedWords.forEach { word ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(ElectricIndigo.copy(alpha = 0.2f))
                                .border(BorderStroke(2.dp, ElectricIndigo), RoundedCornerShape(12.dp))
                                .clickable { onChipDeselected(word) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = word,
                                color = OnSurfaceLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                style = TextStyle(textDirection = TextDirection.Ltr)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Word Bank Chips Pool
        FlowRowLayout(
            horizontalGap = 8.dp,
            verticalGap = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            remainingWords.forEach { word ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurfaceHigh)
                        .border(BorderStroke(2.dp, Color.White.copy(alpha = 0.1f)), RoundedCornerShape(12.dp))
                        .clickable { onChipSelected(word) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = word,
                        color = OnSurfaceLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        style = TextStyle(textDirection = TextDirection.Ltr)
                    )
                }
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
        // Arabic prompt
        Text(
            text = prompt,
            color = OnSurfaceLight,
            fontWeight = FontWeight.Black,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        // Original Sentence Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(ElectricIndigo.copy(alpha = 0.1f))
                .border(BorderStroke(2.dp, ElectricIndigo.copy(alpha = 0.3f)), RoundedCornerShape(16.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "الجملة الأصلية:",
                    color = ElectricIndigo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = originalSentence,
                    color = OnSurfaceLight,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    style = TextStyle(textDirection = TextDirection.Ltr)
                )
            }
        }

        // Instruction
        Text(
            text = "💡 التوجيه: $instructionAr",
            color = GildedAmber,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.fillMaxWidth()
        )

        // Free typing answer
        OutlinedTextField(
            value = textAnswer,
            onValueChange = onAnswerChanged,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                textDirection = TextDirection.Ltr,
                color = OnSurfaceLight,
                fontSize = 16.sp
            ),
            placeholder = { Text("اكتب الإجابة بالإنجليزية هنا...") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = OnSurfaceLight,
                unfocusedTextColor = OnSurfaceLight,
                focusedBorderColor = ElectricIndigo,
                unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface
            ),
            shape = RoundedCornerShape(14.dp),
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
        // Arabic prompt
        Text(
            text = prompt,
            color = OnSurfaceLight,
            fontWeight = FontWeight.Black,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        // Topic Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.05f))
                .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "الموضوع المطلوب:",
                    color = GildedAmber,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    text = topicAr,
                    color = OnSurfaceLight,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
            }
        }

        // Live stats indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "الحد الأدنى: $minWords كلمة",
                color = OnSurfaceVariantLight,
                fontSize = 13.sp
            )

            Text(
                text = "العدد الحالي: $wordCount كلمة",
                color = if (wordCount >= minWords) DuolingoGreen else DuolingoRed,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp
            )
        }

        // Area for long typing
        OutlinedTextField(
            value = textAnswer,
            onValueChange = onAnswerChanged,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            textStyle = TextStyle(
                textDirection = TextDirection.Ltr,
                color = OnSurfaceLight,
                fontSize = 16.sp
            ),
            placeholder = { Text("اكتب موضوعك بالإنجليزية هنا بالتفصيل...") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = OnSurfaceLight,
                unfocusedTextColor = OnSurfaceLight,
                focusedBorderColor = ElectricIndigo,
                unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface
            ),
            shape = RoundedCornerShape(16.dp)
        )
    }
}

// --- Main polymorphic Exercise Card router ---
@Composable
fun ExerciseCardRouter(
    exercise: ExerciseEntity,
    viewModel: LingoMindViewModel,
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

    // Render corresponding screen based on polymorphic exercise type
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
                    CircularProgressIndicator()
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
                    CircularProgressIndicator()
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
                    CircularProgressIndicator()
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
                    CircularProgressIndicator()
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
                    CircularProgressIndicator()
                }
            }
        }
    }
}

// --- FlowRow Layout Composable for layout-safe words wrapping ---
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
