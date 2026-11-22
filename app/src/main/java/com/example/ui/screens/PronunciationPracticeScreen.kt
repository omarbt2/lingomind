package com.example.ui.screens

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PronunciationTargetEntity
import com.example.ui.semaViewModel
import com.example.ui.components.*
import com.example.ui.theme.*
import java.io.File
import java.util.UUID

@Composable
fun PronunciationPracticeScreen(
    viewModel: semaViewModel,
    lessonId: Int,
    onBack: () -> Unit
) {
    val pronunciationTargets by viewModel.pronunciationTargets.collectAsState()
    var currentIndex by remember { mutableIntStateOf(0) }
    var isRecording by remember { mutableStateOf(false) }
    var audioFile by remember { mutableStateOf<String?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var lastScore by remember { mutableStateOf<Float?>(null) }

    val context = LocalContext.current
    var mediaRecorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    LaunchedEffect(lessonId) {
        viewModel.fetchPronunciationTargets(lessonId)
    }

    val currentTarget = pronunciationTargets.getOrNull(currentIndex)

    DisposableEffect(Unit) {
        onDispose {
            mediaRecorder?.release()
            mediaPlayer?.release()
        }
    }

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
                text = "تمرين النطق",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            Box(modifier = Modifier.size(48.dp))
        }

        if (pronunciationTargets.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("لا توجد تمارين نطق متاحة", color = TextSecondary, style = MaterialTheme.typography.bodyLarge)
            }
            return
        }

        Spacer(Modifier.height(16.dp))

        // Progress indicator
        LinearProgressIndicator(
            progress = { (currentIndex + 1).toFloat() / pronunciationTargets.size },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = PrimaryBlue,
            trackColor = SurfaceDark
        )

        Spacer(Modifier.height(8.dp))
        Text(
            text = "${currentIndex + 1} / ${pronunciationTargets.size}",
            color = TextSecondary,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(32.dp))

        // Target phrase display
        if (currentTarget != null) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "قل الجملة التالية:",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = currentTarget.word_or_phrase,
                        color = TextPrimary,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    if (currentTarget.ipa != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "/${currentTarget.ipa}/",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Tips
            if (currentTarget.tip_ar != null || currentTarget.common_error_ar != null) {
                Spacer(Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = XpYellow.copy(alpha = 0.1f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (currentTarget.tip_ar != null) {
                            Text("نصيحة: ${currentTarget.tip_ar}", color = TextPrimary, style = MaterialTheme.typography.bodySmall)
                        }
                        if (currentTarget.common_error_ar != null) {
                            Spacer(Modifier.height(8.dp))
                            Text("خطأ شائع: ${currentTarget.common_error_ar}", color = ErrorRed, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // Recording controls
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Record / Play button
                    Button(
                        onClick = {
                            if (isRecording) {
                                // Stop recording
                                mediaRecorder?.apply {
                                    stop()
                                    release()
                                }
                                mediaRecorder = null
                                isRecording = false
                                // Placeholder score
                                lastScore = (70..100).random().toFloat()
                                viewModel.insertPronunciationAttempt(
                                    currentTarget.id,
                                    audioFile ?: "",
                                    lastScore ?: 0f
                                )
                            } else {
                                // Start recording
                                val fileName = "pronunciation_${UUID.randomUUID()}.3gp"
                                val file = File(context.cacheDir, fileName)
                                audioFile = file.absolutePath
                                try {
                                    mediaRecorder = MediaRecorder().apply {
                                        setAudioSource(MediaRecorder.AudioSource.MIC)
                                        setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                                        setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                                        setOutputFile(file.absolutePath)
                                        prepare()
                                        start()
                                    }
                                    isRecording = true
                                } catch (e: Exception) {
                                    isRecording = false
                                }
                            }
                        },
                        shape = CircleShape,
                        modifier = Modifier.size(80.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRecording) ErrorRed else PrimaryBlue
                        )
                    ) {
                        Icon(
                            imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = if (isRecording) "إيقاف التسجيل" else "تسجيل",
                            modifier = Modifier.size(36.dp),
                            tint = TextPrimary
                        )
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (isRecording) "سجّل الآن..." else "اضغط للتسجيل",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )

                    // Score display
                    if (lastScore != null && !isRecording) {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "التقييم: ${lastScore?.toInt()}%",
                            color = if (lastScore!! >= 70) SuccessGreen else XpYellow,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (currentIndex > 0) {
                    semaButton(
                        onClick = { currentIndex--; lastScore = null; audioFile = null },
                        backgroundColor = SurfaceDark
                    ) {
                        Text("السابق")
                    }
                } else {
                    Spacer(Modifier.width(1.dp))
                }

                if (currentIndex < pronunciationTargets.size - 1) {
                    semaButton(onClick = { currentIndex++; lastScore = null; audioFile = null }) {
                        Text("التالي")
                    }
                } else {
                    semaButton(onClick = onBack) {
                        Text("إنهاء")
                    }
                }
            }
        }
    }
}