package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.BuildConfig
import com.example.data.SupabaseClient
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

data class FlashcardItem(
    val id: Int,
    val front: String,
    val back: String,
    val deckName: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardReviewScreen(
    deckName: String,
    flashcards: List<FlashcardItem>,
    onFinished: () -> Unit,
    onBack: () -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    var showRatingButtons by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val totalCards = flashcards.size
    val currentCard = flashcards.getOrNull(currentIndex)
    val progress = if (totalCards > 0) (currentIndex + 1).toFloat() / totalCards else 0f

    // Flip animation
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "flip"
    )

    // Show rating buttons after flip completes
    LaunchedEffect(isFlipped) {
        if (isFlipped) {
            delay(450)
            showRatingButtons = true
        } else {
            showRatingButtons = false
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = deckName,
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary
                        )
                    },
                    navigationIcon = {
                        semaIconButton(
                            onClick = onBack,
                            content = {
                                Icon(
                                    imageVector = semaIcons.arrowBack(),
                                    contentDescription = "رجوع",
                                    tint = TextPrimary
                                )
                            }
                        )
                    },
                    actions = {
                        Text(
                            text = "${currentIndex + 1} / $totalCards",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextSecondary,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = BackgroundDark
                    )
                )
            },
            containerColor = BackgroundDark
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Progress bar
                semaProgressBar(
                    progress = progress,
                    color = PrimaryBlue,
                    label = null,
                    modifier = Modifier.height(10.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (currentCard != null) {
                    // Flashcard with flip animation
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.75f)
                            .graphicsLayer {
                                rotationY = rotation
                                cameraDistance = 12f * density
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (rotation <= 90f) {
                            // Front face
                            FlashcardFace(
                                text = currentCard.front,
                                label = "الوجه الأمامي",
                                isClickable = true,
                                onClick = { isFlipped = true }
                            )
                        } else {
                            // Back face (rotated 180 to appear correct)
                            Box(
                                modifier = Modifier.graphicsLayer { rotationY = 180f }
                            ) {
                                FlashcardFace(
                                    text = currentCard.back,
                                    label = "الترجمة",
                                    isClickable = false,
                                    onClick = {}
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Instruction text
                    AnimatedVisibility(
                        visible = !isFlipped,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(
                            text = "اضغط على البطاقة لرؤية الإجابة",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Rating buttons
                    AnimatedVisibility(
                        visible = showRatingButtons,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                        exit = fadeOut()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "كيف كان سهولتك؟",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                semaButton(
                                    onClick = {
                                        submitReview(
                                            context, scope, currentCard.id, 2
                                        ) {
                                            advanceCard(
                                                currentIndex, totalCards,
                                                { currentIndex = it },
                                                { isFlipped = false },
                                                onFinished
                                            )
                                        }
                                    },
                                    backgroundColor = SuccessGreen,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "سهل",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = TextPrimary
                                    )
                                }

                                semaButton(
                                    onClick = {
                                        submitReview(
                                            context, scope, currentCard.id, 3
                                        ) {
                                            advanceCard(
                                                currentIndex, totalCards,
                                                { currentIndex = it },
                                                { isFlipped = false },
                                                onFinished
                                            )
                                        }
                                    },
                                    backgroundColor = XpYellow,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "متوسط",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = TextPrimary
                                    )
                                }

                                semaButton(
                                    onClick = {
                                        submitReview(
                                            context, scope, currentCard.id, 1
                                        ) {
                                            advanceCard(
                                                currentIndex, totalCards,
                                                { currentIndex = it },
                                                { isFlipped = false },
                                                onFinished
                                            )
                                        }
                                    },
                                    backgroundColor = ErrorRed,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "صعب",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = TextPrimary
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Empty / finished state
                    Spacer(modifier = Modifier.height(120.dp))

                    EmptyState(
                        icon = semaIcons.close(),
                        title = "لا توجد بطاقات",
                        message = "لم يتم العثور على بطاقات للمراجعة في هذا الرصيد"
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun FlashcardFace(
    text: String,
    label: String,
    isClickable: Boolean,
    onClick: () -> Unit
) {
    semaCard(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .then(
                if (isClickable) Modifier else Modifier
            ),
        highlighted = isClickable
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun submitReview(
    context: android.content.Context,
    scope: kotlinx.coroutines.CoroutineScope,
    flashcardId: Int,
    quality: Int,
    onSuccess: () -> Unit
) {
    val userId = SupabaseClient.getCurrentUserId() ?: return
    scope.launch {
        try {
            val url = "${BuildConfig.SUPABASE_URL}/rest/v1/rpc/update_flashcard_review"
            val json = """{"p_user_id":"${SupabaseClient.jsonEscapeExternal(userId)}","p_flashcard_id":$flashcardId,"p_quality":$quality}"""
            val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

            val token = SupabaseClient.getAccessToken()
            val request = okhttp3.Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("apikey", BuildConfig.SUPABASE_ANON_KEY)
                .addHeader("Authorization", "Bearer ${token ?: BuildConfig.SUPABASE_ANON_KEY}")
                .addHeader("Content-Type", "application/json")
                .build()

            SupabaseClient.getHttpClientExternal().newCall(request).execute()
        } catch (_: Exception) {
            // Silent fail for prototype
        }
        onSuccess()
    }
}

private fun advanceCard(
    currentIndex: Int,
    totalCards: Int,
    setCurrentIndex: (Int) -> Unit,
    setIsFlipped: (Boolean) -> Unit,
    onFinished: () -> Unit
) {
    setIsFlipped(false)
    if (currentIndex + 1 < totalCards) {
        setCurrentIndex(currentIndex + 1)
    } else {
        onFinished()
    }
}
