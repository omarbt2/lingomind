package com.example.ui.components

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Check
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.random.Random

// Haptic feedback utility
object HapticHelper {
    private fun getVibrator(context: android.content.Context): Vibrator? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(VibratorManager::class.java)
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Vibrator::class.java)
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun hasVibrator(context: android.content.Context): Boolean {
        return try {
            val vibrator = getVibrator(context)
            vibrator?.hasVibrator() == true
        } catch (e: Exception) {
            false
        }
    }

    fun performHapticClick(context: android.content.Context) {
        if (!hasVibrator(context)) return
        try {
            getVibrator(context)?.let { vibrator ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(20)
                }
            }
        } catch (e: Exception) {
        }
    }

    fun performHapticSuccess(context: android.content.Context) {
        if (!hasVibrator(context)) return
        try {
            getVibrator(context)?.let { vibrator ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val effect = VibrationEffect.createWaveform(
                        longArrayOf(0, 50, 50, 50),
                        intArrayOf(0, 50, 0, 100),
                        -1
                    )
                    vibrator.vibrate(effect)
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(longArrayOf(0, 50, 50, 50), -1)
                }
            }
        } catch (e: Exception) {
        }
    }

    fun performHapticError(context: android.content.Context) {
        if (!hasVibrator(context)) return
        try {
            getVibrator(context)?.let { vibrator ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(100)
                }
            }
        } catch (e: Exception) {
        }
    }
}

// Loading skeleton composable
@Composable
fun LoadingSkeleton(
    modifier: Modifier = Modifier,
    width: Float = 1f,
    height: Float = 1f,
    cornerRadius: Int = 8
) {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "skeleton_alpha"
    )

    Box(
        modifier = modifier
            .alpha(alpha)
            .clip(RoundedCornerShape(cornerRadius))
            .background(SurfaceVariant)
    )
}

// Skeleton for profile screen
@Composable
fun ProfileSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LoadingSkeleton(
                modifier = Modifier.size(80.dp),
                cornerRadius = 40
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LoadingSkeleton(
                    modifier = Modifier.width(120.dp).height(24.dp)
                )
                LoadingSkeleton(
                    modifier = Modifier.width(80.dp).height(16.dp)
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(2) {
                LoadingSkeleton(
                    modifier = Modifier.weight(1f).height(80.dp),
                    cornerRadius = 16
                )
            }
        }

        repeat(2) {
            LoadingSkeleton(
                modifier = Modifier.fillMaxWidth().height(60.dp),
                cornerRadius = 16
            )
        }
    }
}

// Empty state composable
@Composable
fun EmptyState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(SurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(40.dp)
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary
        )

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

// Success animation overlay
@Composable
fun SuccessOverlay(
    show: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = show,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(BackgroundDark.copy(alpha = 0.9f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(SuccessGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = semaIcons.check(),
                        contentDescription = "Success",
                        tint = SuccessGreen,
                        modifier = Modifier.size(80.dp)
                    )
                }

                Text(
                    text = "أحسنت!",
                    style = MaterialTheme.typography.displayMedium,
                    color = SuccessGreen
                )
            }
        }
    }
}

// Animated counter
@Composable
fun AnimatedCounter(
    targetValue: Int,
    modifier: Modifier = Modifier,
    color: Color = TextPrimary,
    textStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.headlineMedium
) {
    val animatedValue by animateIntAsState(
        targetValue = targetValue,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "counter"
    )

    Text(
        text = animatedValue.toString(),
        style = textStyle,
        color = color,
        modifier = modifier
    )
}

// ===== SHARED ANIMATION PRIMITIVES =====

// Content fade-in wrapper — fades + slides content in once loaded
@Composable
fun FadeInContent(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(400)) +
                slideInVertically(animationSpec = tween(400), initialOffsetY = { it / 8 }),
        modifier = modifier,
        content = content
    )
}

// Staggered entrance — wraps content with fade+slide-up, delayed by index
@Composable
fun StaggeredEntrance(
    index: Int,
    staggerDelayMs: Long = 60L,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(staggerDelayMs * index)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(400)) +
                slideInVertically(
                    animationSpec = tween(400),
                    initialOffsetY = { it / 4 }
                ),
        modifier = modifier,
        content = content
    )
}

// Pulse animation — looping soft scale pulse
@Composable
fun PulseAnimation(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = modifier.graphicsLayer { scaleX = scale; scaleY = scale },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

// Shake animation — use in a Box/Column, pass the trigger flag
@Composable
fun ShakeAnimation(
    shake: Boolean,
    onShakeEnd: () -> Unit = {},
    content: @Composable (Modifier) -> Unit
) {
    val offsetX = remember { Animatable(0f) }
    val density = LocalDensity.current

    LaunchedEffect(shake) {
        if (shake) {
            val amplitude = with(density) { 8.dp.toPx() }
            offsetX.animateTo(amplitude, tween(50))
            offsetX.animateTo(-amplitude, tween(50))
            offsetX.animateTo(amplitude * 0.5f, tween(50))
            offsetX.animateTo(-amplitude * 0.5f, tween(50))
            offsetX.animateTo(0f, tween(50))
            onShakeEnd()
        }
    }

    content(Modifier.graphicsLayer { translationX = offsetX.value })
}

// Float-up text that self-removes after animation
@Composable
fun FloatUpText(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(true) }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { 0 }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it / 2 }) + fadeOut()
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.titleMedium,
            modifier = modifier
        )
    }

    LaunchedEffect(Unit) {
        delay(800)
        visible = false
    }
}

// Heart loss animation — animates heart icon shrink + fade
@Composable
fun AnimatedHeartLoss(
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(true) }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Icon(
            imageVector = semaIcons.favorite(),
            contentDescription = null,
            tint = ErrorRed,
            modifier = modifier.size(24.dp)
        )
    }

    LaunchedEffect(Unit) {
        delay(600)
        visible = false
    }
}

// Correct answer bounce animation
@Composable
fun CorrectAnswerBounce(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val scale = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        scale.animateTo(1.08f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
        scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    }

    Box(
        modifier = modifier.graphicsLayer { scaleX = scale.value; scaleY = scale.value },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

// Border color animation helper
@Composable
fun AnimatedBorderColor(
    targetColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(300),
        label = "borderColor"
    )

    Box(modifier = modifier) {
        content()
    }
}
