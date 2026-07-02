package com.rajumark.newzero.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Slate = Color(0xFF5A6577)
private val SlateLight = Color(0xFFE8EBF0)
private val White = Color(0xFFFFFFFF)
private val OffWhite = Color(0xFFF8F9FA)
private val NearBlack = Color(0xFF1A1C1E)
private val DarkGray = Color(0xFF2C3037)
private val MediumGray = Color(0xFF3A3F48)
private val BorderLight = Color(0xFFE0E2E6)
private val BorderDark = Color(0xFF383C44)

private val LightColors = lightColorScheme(
    primary = Slate,
    onPrimary = White,
    secondary = Slate,
    onSecondary = White,
    surface = White,
    onSurface = NearBlack,
    surfaceVariant = OffWhite,
    onSurfaceVariant = Slate,
    outline = BorderLight,
    primaryContainer = OffWhite,
    onPrimaryContainer = NearBlack
)

private val DarkColors = darkColorScheme(
    primary = Slate,
    onPrimary = White,
    secondary = Slate,
    onSecondary = White,
    surface = NearBlack,
    onSurface = White,
    surfaceVariant = DarkGray,
    onSurfaceVariant = Slate,
    outline = BorderDark,
    primaryContainer = DarkGray,
    onPrimaryContainer = White
)

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = {
            Surface(content = content)
        }
    )
}
