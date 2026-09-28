package org.chornobyl.hamdash.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Color0 = Color(0xFF00120A)

private val HamDarkColorScheme = darkColorScheme(
    primary = HamGreen,
    onPrimary = Color0,
    secondary = HamAmber,
    onSecondary = Color0,
    tertiary = HamCyan,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnBackground,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceMuted,
    outline = DarkOutline,
    error = HamRed,
)

private val HamLightColorScheme = lightColorScheme(
    primary = HamGreenDim,
    secondary = HamAmber,
    tertiary = HamCyan,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnBackground,
    error = HamRed,
)

/** App defaults to dark, ham-dashboard styling regardless of system theme unless the
 * user explicitly opts into Light in Settings. [useDarkTheme] is driven by the
 * persisted [org.chornobyl.hamdash.settings.AppTheme] setting. */
@Composable
fun ChornobylHamDashTheme(
    useDarkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (useDarkTheme) HamDarkColorScheme else HamLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = HamTypography,
        content = content,
    )
}

@Composable
fun isSystemDarkAsFallback(): Boolean = isSystemInDarkTheme()
