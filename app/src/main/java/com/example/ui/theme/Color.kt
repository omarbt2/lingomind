package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// sema Design Tokens — Duosema-style dark theme
// ONLY these colors are allowed app-wide. No hardcoded hex in composables.

val PrimaryBlue = Color(0xFF1CB0F6)
val SuccessGreen = Color(0xFF58CC02)
val ErrorRed = Color(0xFFFF4B4B)
val XpYellow = Color(0xFFFFC800)
val BackgroundDark = Color(0xFF0F1720)
val SurfaceDark = Color(0xFF1A2433)
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFF8A94A6)

// Semantic aliases for Material3 color scheme
val Primary = PrimaryBlue
val Secondary = SuccessGreen
val Tertiary = XpYellow
val Error = ErrorRed
val Background = BackgroundDark
val Surface = SurfaceDark
val SurfaceVariant = Color(0xFF232E3F)
val OnPrimary = TextPrimary
val OnSecondary = TextPrimary
val OnBackground = TextPrimary
val OnSurface = TextPrimary
val OnSurfaceVariant = TextSecondary
val Outline = Color(0xFF2A3546)
val OutlineVariant = Color(0xFF1F2937)
