package com.medsurgery.kiruplus.core.designsystem

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * KIRU+ Typography — espejo de `Core/DesignSystem/KiruTypography.swift` (iOS).
 *
 * Escala "Steve Jobs Readable": +2pt sobre Material 3 default, line-height 1.5–1.65
 * para legibilidad médica. iOS letter-spacing en em → Compose lo expresa en sp.
 * Conversión: spacing_sp = em × fontSize_pt.
 */
internal val KiruTypography = Typography(
    // iOS displayLarge: 40pt bold, lineHeight 1.15, letterSpacing -0.02 em
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        lineHeight = 46.sp,
        letterSpacing = (-0.8).sp,
    ),
    // iOS displayMedium: 32pt bold, lineHeight 1.2, letterSpacing -0.015 em
    displayMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 38.sp,
        letterSpacing = (-0.48).sp,
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.42).sp,
    ),
    // iOS headlineLarge: 24pt bold, lineHeight 1.3, letterSpacing -0.005 em
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 31.sp,
        letterSpacing = (-0.12).sp,
    ),
    // iOS headlineMedium: 20pt semibold, lineHeight 1.35, letterSpacing 0
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 27.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 25.sp,
    ),
    // iOS roundedHeadline: 17pt semibold rounded → Android Material titleLarge
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 25.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    // iOS bodyLarge: 19pt regular, lineHeight 1.65
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 19.sp,
        lineHeight = 31.sp,
    ),
    // iOS bodyMedium: 18pt medium, lineHeight 1.65
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 30.sp,
    ),
    // iOS bodyRegular: 18pt regular, lineHeight 1.65
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.16.sp,
    ),
    // iOS labelRegular: 16pt regular, lineHeight 1.5, letterSpacing 0.01 em
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.16.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.14.sp,
    ),
    // iOS captionSmall: 14pt regular, lineHeight 1.5, letterSpacing 0.015 em
    // iOS allCapsSmall: 12pt semibold, letterSpacing 0.05 em → labelSmall en buttons
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.6.sp,
    ),
)
