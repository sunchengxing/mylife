package com.mylife.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF1D1D1F),
    onPrimary = Color.White,
    secondary = Color(0xFF6E6E73),
    background = Color(0xFFF5F5F7),
    surface = Color.White,
    onBackground = Color(0xFF1D1D1F),
    onSurface = Color(0xFF1D1D1F),
    surfaceVariant = Color(0xFFE8E8ED),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFF5F5F7),
    onPrimary = Color(0xFF1D1D1F),
    secondary = Color(0xFF98989D),
    background = Color(0xFF1D1D1F),
    surface = Color(0xFF2C2C2E),
    onBackground = Color(0xFFF5F5F7),
    onSurface = Color(0xFFF5F5F7),
    surfaceVariant = Color(0xFF3A3A3C),
)

@Composable
fun MyLifeTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
