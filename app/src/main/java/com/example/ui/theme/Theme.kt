package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = RoyalCobalt,
    secondary = GlacierMist,
    tertiary = GildedAmber,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceHigh,
    onPrimary = Color.White,
    onSecondary = Color(0xFF020B1C),
    onBackground = OnSurfaceLight,
    onSurface = OnSurfaceLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineVariantLight,
    outlineVariant = OutlineVariantLight
)

private val LightColorScheme = lightColorScheme(
    primary = RoyalCobalt,
    secondary = GlacierMist,
    tertiary = GildedAmber,
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFF0F4F8),
    surfaceVariant = Color(0xFFDBE2E9),
    onPrimary = Color.White,
    onSecondary = RoyalCobalt,
    onBackground = Color(0xFF020B1C),
    onSurface = Color(0xFF020B1C),
    onSurfaceVariant = Color(0xFF556A80),
    outline = Color(0xFFDBE2E9),
    outlineVariant = Color(0xFFDBE2E9)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Disable dynamicColor to enforce LingoMind's brand design tokens exactly
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> DarkColorScheme // Force dark theme by default since Duolingo-styled UI is styled in premium dark mode in the prompts and screenshots!
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
