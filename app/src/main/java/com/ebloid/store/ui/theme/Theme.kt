package com.ebloid.store.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Accent = Color(0xFF3DDC84)

private val DarkColors = darkColorScheme(
    primary = Accent,
    onPrimary = Color(0xFF06210F),
    background = Color(0xFF0F1115),
    surface = Color(0xFF1A1D24),
    onBackground = Color(0xFFE7EBF0),
    onSurface = Color(0xFFE7EBF0),
    surfaceVariant = Color(0xFF2A2F3A),
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF1E9E5A),
    background = Color(0xFFF5F7FA),
    surface = Color(0xFFFFFFFF),
)

@Composable
fun EbloidTheme(content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    MaterialTheme(
        colorScheme = if (dark) DarkColors else LightColors,
        content = content,
    )
}
