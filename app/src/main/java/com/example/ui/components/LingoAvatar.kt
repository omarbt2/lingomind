package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

// semaAvatar — circular avatar with optional background color
@Composable
fun semaAvatar(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    backgroundColor: Color = PrimaryBlue,
    content: @Composable () -> Unit = {
        Icon(
            imageVector = semaIcons.person(),
            contentDescription = null,
            tint = TextPrimary,
            modifier = Modifier.size(size * 0.6f)
        )
    }
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
