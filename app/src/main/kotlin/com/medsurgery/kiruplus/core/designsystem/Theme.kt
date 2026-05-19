package com.medsurgery.kiruplus.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.medsurgery.kiruplus.core.prefs.AppTheme

/**
 * Material 3 mapping desde el Brand Manual KIRU+.
 *
 * Light: fondo claro (convención Android), primary navy para legibilidad.
 * Dark: fondo navy (brand-first como iOS), primary cyan para contraste.
 *
 * Semantic mapping (espejo de KiruDesignTokens.Semantic en iOS):
 * - primary       = Navy (light) / Cyan (dark)
 * - secondary     = Teal
 * - tertiary      = Gold (Premium/Pro highlights)
 * - background    = White-ish (light) / Navy (dark)
 * - surface       = Slate-50/200 (light) / Black-191919 (dark)
 * - error         = Red
 */
private val LightColors = lightColorScheme(
    primary = KiruNavyBlue,
    onPrimary = KiruWhite,
    primaryContainer = KiruCyanBlue,
    onPrimaryContainer = KiruNavyBlue,
    secondary = KiruTeal,
    onSecondary = KiruWhite,
    tertiary = KiruGold,
    onTertiary = KiruBlack,
    background = LightSurface,
    onBackground = KiruNavyBlue,
    surface = LightSurface,
    onSurface = KiruNavyBlue,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    error = KiruRed,
    onError = KiruWhite,
    outline = LightOnSurfaceVariant,
)

private val DarkColors = darkColorScheme(
    primary = KiruCyanBlue,
    onPrimary = KiruNavyBlue,
    primaryContainer = KiruNavyBlue,
    onPrimaryContainer = KiruCyanBlue,
    secondary = KiruTeal,
    onSecondary = KiruWhite,
    tertiary = KiruGold,
    onTertiary = KiruBlack,
    background = KiruNavyBlue,
    onBackground = KiruWhite,
    surface = KiruBlack,
    onSurface = KiruWhite,
    surfaceVariant = KiruSlateGray,
    onSurfaceVariant = KiruWhite,
    error = KiruRed,
    onError = KiruWhite,
    outline = KiruSlateGray,
)

@Composable
fun KiruTheme(
    appTheme: AppTheme = AppTheme.System,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (appTheme) {
        AppTheme.System -> isSystemInDarkTheme()
        AppTheme.Light -> false
        AppTheme.Dark -> true
    }
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = KiruTypography,
        content = content,
    )
}
