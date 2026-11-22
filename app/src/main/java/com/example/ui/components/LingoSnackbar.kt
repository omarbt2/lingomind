package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

// semaSnackbar — styled snackbar for success/error/info messages
@Composable
fun semaSnackbar(
    message: String,
    type: SnackbarType = SnackbarType.Info,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (type) {
        SnackbarType.Success -> SuccessGreen.copy(alpha = 0.15f) to SuccessGreen
        SnackbarType.Error -> ErrorRed.copy(alpha = 0.15f) to ErrorRed
        SnackbarType.Info -> PrimaryBlue.copy(alpha = 0.15f) to PrimaryBlue
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(bgColor)
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = message,
            color = textColor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

enum class SnackbarType {
    Success,
    Error,
    Info
}
