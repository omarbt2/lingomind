package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.semaViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun WritingPracticeScreen(
    viewModel: semaViewModel,
    onBack: () -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var textAnswer by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    val topics = listOf(
        "Describe your favorite hobby and why you enjoy it",
        "Write about your daily routine in English",
        "Describe your dream vacation destination",
        "What are your goals for learning English?",
        "Describe a person who has influenced your life"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(16.dp)
    ) {
        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            semaIconButton(onClick = onBack) {
                Icon(semaIcons.arrowBack(), contentDescription = "رجوع", tint = TextPrimary)
            }
            Text(
                text = "تمرين الكتابة",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            Box(modifier = Modifier.size(48.dp))
        }

        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Instructions card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = semaIcons.style(),
                        contentDescription = "كتابة",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "اكتب موضوعاً باللغة الإنجليزية",
                        color = TextPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "حاول استخدام أكبر عدد ممكن من المفردات الجديدة والقواعد التي تعلمتها",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Topic suggestions
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "اختر موضوعاً للكتابة:",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(12.dp))

                    topics.forEachIndexed { index, topic ->
                        val isSelected = currentIndex == index
                        Surface(
                            onClick = { currentIndex = index; submitted = false; textAnswer = "" },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) PrimaryBlue.copy(alpha = 0.15f) else BackgroundDark,
                            border = null
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) PrimaryBlue else SurfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        color = TextPrimary,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                                Text(
                                    text = topic,
                                    color = if (isSelected) TextPrimary else TextSecondary,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Writing area
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "مساحة الكتابة:",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = textAnswer,
                        onValueChange = { textAnswer = it },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 200.dp),
                        placeholder = { Text("ابدأ الكتابة هنا...", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = PrimaryBlue,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = SurfaceVariant,
                            focusedContainerColor = BackgroundDark,
                            unfocusedContainerColor = BackgroundDark
                        ),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 20
                    )

                    Spacer(Modifier.height(8.dp))

                    // Word count
                    val wordCount = textAnswer.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }.size
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "عدد الكلمات: $wordCount",
                            color = if (wordCount >= 20) SuccessGreen else TextSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "الحد الأدنى: 20 كلمة",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Submission status
            if (submitted) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = semaIcons.checkCircle(),
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "تم إرسال كتابتك!",
                                color = SuccessGreen,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "سيتم تقييمها من قبل المساعد الذكي قريباً",
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        // Submit button
        semaButton(
            onClick = {
                val wordCount = textAnswer.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }.size
                viewModel.insertWritingSubmission(
                    exerciseId = currentIndex + 1,
                    topicAr = topics[currentIndex],
                    userText = textAnswer.trim(),
                    wordCount = wordCount
                )
                submitted = true
            },
            enabled = textAnswer.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }.size >= 20 && !submitted,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            Text(
                text = if (!submitted) "إرسال" else "تم الإرسال",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}