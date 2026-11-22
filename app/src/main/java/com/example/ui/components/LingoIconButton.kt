package com.example.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

@Composable
fun semaIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = PrimaryBlue.copy(alpha = 0.15f),
    contentColor: Color = PrimaryBlue,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    var isPressed by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val offsetVal by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 0.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "icon_button_press"
    )

    Box(
        modifier = modifier
            .size(40.dp)
            .padding(bottom = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .offset(y = 3.dp)
                .clip(CircleShape)
                .background(if (enabled) backgroundColor.copy(alpha = 0.5f) else Color.Transparent)
        )
        Box(
            modifier = Modifier
                .size(40.dp)
                .offset(y = offsetVal)
                .clip(CircleShape)
                .background(if (enabled) backgroundColor else Color(0xFF374151))
                .clickable(
                    enabled = enabled,
                    onClick = {
                        HapticHelper.performHapticClick(context)
                        onClick()
                    },
                    interactionSource = interactionSource,
                    indication = null
                ),
            contentAlignment = Alignment.Center
        ) {
            CompositionLocalProvider(
                LocalContentColor provides if (enabled) contentColor else TextSecondary
            ) {
                Box(modifier = Modifier.size(20.dp), contentAlignment = Alignment.Center) {
                    content()
                }
            }
        }
    }
}
