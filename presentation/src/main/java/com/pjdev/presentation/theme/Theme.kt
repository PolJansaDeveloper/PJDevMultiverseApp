package com.pjdev.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = DeepPortalGreen,
    onPrimary = LightSurface,
    primaryContainer = PortalGreen,
    onPrimaryContainer = LightOnBackground,

    secondary = DimensionCyan,
    onSecondary = LightOnBackground,

    background = LightBackground,
    onBackground = LightOnBackground,

    surface = LightSurface,
    onSurface = LightOnBackground,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,

    outline = LightOutline,

    error = LightError,
    onError = LightSurface,
)

private val DarkColorScheme = darkColorScheme(
    primary = PortalGreen,
    onPrimary = DarkBackground,
    primaryContainer = DeepPortalGreen,
    onPrimaryContainer = DarkOnBackground,

    secondary = DimensionCyan,
    onSecondary = DarkBackground,

    background = DarkBackground,
    onBackground = DarkOnBackground,

    surface = DarkSurface,
    onSurface = DarkOnBackground,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,

    outline = DarkOutline,

    error = DarkError,
    onError = DarkBackground,
)

@Composable
fun MultiverseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = MultiverseShapes,
        content = content,
    )
}
