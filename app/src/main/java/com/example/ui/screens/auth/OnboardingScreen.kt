package com.example.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SupabaseClient
import com.example.data.saveInterestsSuspend
import com.example.data.saveLevelSuspend
import com.example.ui.components.semaButton
import com.example.ui.components.semaCard
import com.example.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material3.CircularProgressIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    onSkip: () -> Unit
) {
    var currentStep by remember { mutableStateOf(0) }
    var selectedLevel by remember { mutableStateOf<String?>(null) }
    var selectedInterests by remember { mutableStateOf<Set<String>>(emptySet()) }
    val isLoading = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    val levels = listOf(
        "beginner" to "Beginner (مبتدئ)",
        "intermediate" to "Intermediate (متوسط)",
        "advanced" to "Advanced (متقدم)"
    )
    val interests = listOf(
        "Travel", "Work", "Tech", "Science",
        "Arts", "Sports", "Cooking", "Music",
        "Movies", "Reading", "History", "Nature"
    )

    fun doSaveLevel(
        selectedLevel: String,
        isLoading: MutableState<Boolean>,
        errorMessage: MutableState<String?>,
        onComplete: () -> Unit,
    ) {
        val token = SupabaseClient.getAccessToken()
        if (token == null) {
            errorMessage.value = "Session expired. Please log in again."
            return
        }
        isLoading.value = true
        errorMessage.value = null

        CoroutineScope(Dispatchers.IO).launch {
            val result = SupabaseClient.saveLevelSuspend(
                accessToken = token,
                level = selectedLevel
            )

            kotlinx.coroutines.withContext(Dispatchers.Main) {
                if (result.isSuccess) {
                    onComplete()
                } else {
                    errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to save level"
                }
                isLoading.value = false
            }
        }
    }

    fun doSubmitInterests(
        selectedInterests: Set<String>,
        isLoading: MutableState<Boolean>,
        errorMessage: MutableState<String?>,
        onComplete: () -> Unit,
    ) {
        if (selectedInterests.size < 3) return
        val token = SupabaseClient.getAccessToken()
        if (token == null) {
            errorMessage.value = "Session expired. Please log in again."
            return
        }
        isLoading.value = true
        errorMessage.value = null

        CoroutineScope(Dispatchers.IO).launch {
            val result = SupabaseClient.saveInterestsSuspend(
                accessToken = token,
                interests = selectedInterests.toList()
            )

            kotlinx.coroutines.withContext(Dispatchers.Main) {
                if (result.isSuccess) {
                    onComplete()
                } else {
                    errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to save interests"
                }
                isLoading.value = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .background(BackgroundDark)
    ) {
        // Progress indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            (0..2).forEach { step ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (step <= currentStep) PrimaryBlue else Color.White.copy(alpha = 0.1f)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Step content
        when (currentStep) {
            0 -> WelcomeStep(onContinue = { currentStep = 1 })
            1 -> LevelSelectionStep(
                levels = levels,
                selectedLevel = selectedLevel,
                isLoading = isLoading,
                errorMessage = errorMessage,
                onLevelSelect = { level ->
                    selectedLevel = level
                    doSaveLevel(
                        selectedLevel = level,
                        isLoading = isLoading,
                        errorMessage = errorMessage,
                        onComplete = { currentStep = 2 }
                    )
                }
            )
            2 -> InterestsSelectionStep(
                interests = interests,
                selectedInterests = selectedInterests,
                onInterestToggle = { interest ->
                    selectedInterests = if (selectedInterests.contains(interest)) {
                        selectedInterests - interest
                    } else {
                        selectedInterests + interest
                    }
                },
                isLoading = isLoading,
                errorMessage = errorMessage,
                onContinue = {
                    if (selectedInterests.size >= 3) {
                        doSubmitInterests(
                            selectedInterests = selectedInterests,
                            isLoading = isLoading,
                            errorMessage = errorMessage,
                            onComplete = onComplete,
                        )
                    }
                },
                onBack = { currentStep = 1 }
            )
        }

        // Skip button
        if (currentStep < 2) {
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onSkip) {
                Text("Skip", color = TextSecondary, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun WelcomeStep(onContinue: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "sema",
            style = MaterialTheme.typography.displayLarge,
            color = PrimaryBlue,
            fontWeight = FontWeight.Black
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Welcome! Let's personalize your experience",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "We'll help you build a learning plan that fits your level and interests",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(48.dp))
        semaButton(onClick = onContinue, modifier = Modifier.width(200.dp)) {
            Text("Start", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun LevelSelectionStep(
    levels: List<Pair<String, String>>,
    selectedLevel: String?,
    isLoading: MutableState<Boolean>,
    errorMessage: MutableState<String?>,
    onLevelSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "What is your language level?",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Choose the level that suits you best",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                levels.forEach { (value, label) ->
                    val isSelected = selectedLevel == value
                    semaCard(
                        highlighted = isSelected,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = if (isLoading.value) null else ({ onLevelSelect(value) } as (() -> Unit)?)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(MaterialTheme.shapes.small)
                                    .border(
                                        BorderStroke(2.dp, if (isSelected) PrimaryBlue else Color.White.copy(alpha = 0.2f)),
                                        MaterialTheme.shapes.small
                                    )
                                    .background(if (isSelected) PrimaryBlue else Color.Transparent),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = semaIcons.check(),
                                        contentDescription = "Selected",
                                        tint = TextPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.titleLarge,
                                color = if (isSelected) PrimaryBlue else TextPrimary,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
            if (isLoading.value) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clip(RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryBlue, modifier = Modifier.size(48.dp))
                }
            }
        }

        errorMessage.value?.let { msg ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = msg,
                style = MaterialTheme.typography.bodyMedium,
                color = ErrorRed,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun InterestsSelectionStep(
    interests: List<String>,
    selectedInterests: Set<String>,
    onInterestToggle: (String) -> Unit,
    isLoading: MutableState<Boolean>,
    errorMessage: MutableState<String?>,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "What are your interests?",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Choose at least 3 interests (${selectedInterests.size} selected)",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

        androidx.compose.foundation.lazy.LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
items(interests) { interest ->
                val isSelected = selectedInterests.contains(interest)
                semaCard(
                    highlighted = isSelected,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    onClick = { onInterestToggle(interest) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = interest,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isSelected) PrimaryBlue else TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(MaterialTheme.shapes.small)
                                .border(
                                    BorderStroke(2.dp, if (isSelected) PrimaryBlue else Color.White.copy(alpha = 0.2f)),
                                    MaterialTheme.shapes.small
                                )
                                .background(if (isSelected) PrimaryBlue else Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = semaIcons.check(),
                                    contentDescription = "Selected",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Error message
        errorMessage.value?.let { msg ->
            Text(
                text = msg,
                color = ErrorRed,
                style = MaterialTheme.typography.bodySmall,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Bottom buttons
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .background(BackgroundDark)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                semaButton(
                    onClick = onContinue,
                    enabled = selectedInterests.size >= 3 && !isLoading.value,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isLoading.value) {
                        CircularProgressIndicator(color = TextPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Continue", style = MaterialTheme.typography.titleMedium)
                    }
                }
                TextButton(onClick = onBack) {
                    Text("Back", color = TextSecondary, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
