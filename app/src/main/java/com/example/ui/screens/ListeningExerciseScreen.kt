package com.example.ui.screens

import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.data.ListeningExerciseEntity
import com.example.ui.semaViewModel
import com.example.ui.components.*
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun ListeningExerciseScreen(
    viewModel: semaViewModel,
    lessonId: Int,
    onBack: () -> Unit
) {
    val listeningExercises by viewModel.listeningExercises.collectAsState()
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableIntStateOf(-1) }
    var isPlaying by remember { mutableStateOf(false) }
    var showTranscript by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var textToSpeech by remember { mutableStateOf<TextToSpeech?>(null) }
    var ttsInit by remember { mutableStateOf(false) }

    LaunchedEffect(lessonId) {
        viewModel.fetchListeningExercises(lessonId)
    }

    // Initialize TTS
    LaunchedEffect(Unit) {
        textToSpeech = TextToSpeech(context) { status ->
            ttsInit = (status == TextToSpeech.SUCCESS)
            textToSpeech?.language = Locale.US
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        }
    }

    val currentExercise = listeningExercises.getOrNull(currentIndex)

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
                text = "تمرين الاستماع",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            Box(modifier = Modifier.size(48.dp))
        }

        if (listeningExercises.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
            return
        }

        Spacer(Modifier.height(16.dp))

        // Progress
        LinearProgressIndicator(
            progress = { (currentIndex + 1).toFloat() / listeningExercises.size },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = PrimaryBlue,
            trackColor = SurfaceDark
        )

        Spacer(Modifier.height(8.dp))
        Text(
            text = "${currentIndex + 1} / ${listeningExercises.size}",
            color = TextSecondary,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(24.dp))

        if (currentExercise != null) {
            // Audio player card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "تشغيل الصوت",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(64.dp)
                    )

                    Spacer(Modifier.height(16.dp))

                    // Play button
                    Button(
                        onClick = {
                            if (isPlaying) {
                                mediaPlayer?.pause()
                                textToSpeech?.stop()
                                isPlaying = false
                            } else {
                                val url = currentExercise.audio_url
                                if (url.isNotBlank()) {
                                    try {
                                        mediaPlayer?.release()
                                        mediaPlayer = MediaPlayer().apply {
                                            setDataSource(url)
                                            setOnPreparedListener { start(); isPlaying = true }
                                            setOnCompletionListener { isPlaying = false }
                                            setOnErrorListener { _, _, _ -> isPlaying = false; true }
                                            prepareAsync()
                                        }
                                    } catch (e: Exception) {
                                        // Fallback to TTS
                                        if (ttsInit) {
                                            textToSpeech?.speak(currentExercise.transcript, TextToSpeech.QUEUE_FLUSH, null, "")
                                            isPlaying = true
                                        }
                                    }
                                } else if (ttsInit) {
                                    // No audio URL, use TTS
                                    textToSpeech?.speak(currentExercise.transcript, TextToSpeech.QUEUE_FLUSH, null, "utteranceId")
                                    isPlaying = true
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        modifier = Modifier.fillMaxWidth(0.6f)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "إيقاف" else "تشغيل",
                            tint = TextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(if (isPlaying) "إيقاف" else "تشغيل", color = TextPrimary)
                    }

                    // Transcript toggle
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = { showTranscript = !showTranscript }) {
                        Icon(
                            imageVector = if (showTranscript) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = if (showTranscript) "إخفاء النص" else "عرض النص",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    if (showTranscript) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = currentExercise.transcript,
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Difficulty badge
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "مستوى: ${currentExercise.difficulty}",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
                if (currentExercise.accent != null) {
                    Text(
                        text = "لهجة: ${currentExercise.accent}",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Transcript-based comprehension
            Text(
                text = "استمع إلى النص ثم حاول فهم المعنى. يمكنك مراجعة النص عند الحاجة.",
                color = TextPrimary,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(Modifier.weight(1f))

            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (currentIndex > 0) {
                    semaButton(
                        onClick = { currentIndex--; selectedOption = -1; showTranscript = false },
                        backgroundColor = SurfaceDark
                    ) {
                        Text("السابق")
                    }
                } else {
                    Spacer(Modifier.width(1.dp))
                }

                if (currentIndex < listeningExercises.size - 1) {
                    semaButton(onClick = { currentIndex++; selectedOption = -1; showTranscript = false }) {
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