package com.example.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

// semaButton variant
enum class semaButtonVariant {
    Primary, Outline, Ghost
}

// semaButton — single CTA variant used app-wide
@Composable
fun semaButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: semaButtonVariant = semaButtonVariant.Primary,
    backgroundColor: Color = PrimaryBlue,
    contentColor: Color = TextPrimary,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    var isPressed by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val offsetVal by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 0.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "button_press"
    )

    Box(
        modifier = modifier
            .padding(bottom = 6.dp)
            .clickable(
                enabled = enabled,
                onClick = {
                    HapticHelper.performHapticClick(context)
                    onClick()
                },
                interactionSource = interactionSource,
                indication = null
            )
    ) {
        when (variant) {
            semaButtonVariant.Primary -> {
                // Shadow layer
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .offset(y = 6.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(if (enabled) backgroundColor.copy(alpha = 0.6f) else Color(0xFF374151))
                )

                // Main button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = offsetVal)
                        .clip(MaterialTheme.shapes.medium)
                        .background(if (enabled) backgroundColor else Color(0xFF374151))
                        .border(
                            BorderStroke(2.dp, if (enabled) Color.White.copy(alpha = 0.15f) else Color.Transparent),
                            MaterialTheme.shapes.medium
                        )
                        .padding(vertical = 14.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CompositionLocalProvider(
                        LocalContentColor provides if (enabled) contentColor else TextSecondary
                    ) {
                        content()
                    }
                }
            }

            semaButtonVariant.Outline -> {
                // Shadow layer
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .offset(y = 6.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(if (enabled) backgroundColor.copy(alpha = 0.1f) else Color(0xFF374151).copy(alpha = 0.3f))
                )

                // Main button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = offsetVal)
                        .clip(MaterialTheme.shapes.medium)
                        .background(if (enabled) Color.Transparent else Color(0xFF374151).copy(alpha = 0.3f))
                        .border(
                            BorderStroke(2.dp, if (enabled) backgroundColor else Color.Transparent),
                            MaterialTheme.shapes.medium
                        )
                        .padding(vertical = 14.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CompositionLocalProvider(
                        LocalContentColor provides if (enabled) backgroundColor else TextSecondary
                    ) {
                        content()
                    }
                }
            }

            semaButtonVariant.Ghost -> {
                // Shadow layer
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .offset(y = 6.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color.Transparent)
                )

                // Main button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = offsetVal)
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color.Transparent)
                        .border(
                            BorderStroke(2.dp, Color.Transparent),
                            MaterialTheme.shapes.medium
                        )
                        .padding(vertical = 14.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CompositionLocalProvider(
                        LocalContentColor provides if (enabled) contentColor else TextSecondary
                    ) {
                        content()
                    }
                }
            }
        }
    }
}
