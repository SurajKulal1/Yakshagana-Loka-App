package com.yakshagana.loka.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF16A34A),
    secondary = Color(0xFFC9A227),
    tertiary = Color(0xFF005F73),
    background = Color(0xFFFFF8E7),
    surface = Color(0xFFFFFBF2),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color(0xFF1B1B1B),
    onSurface = Color(0xFF1B1B1B)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFF6F61),
    secondary = Color(0xFFFFD166),
    tertiary = Color(0xFF5CC8FF),
    background = Color(0xFF1A1A1A),
    surface = Color(0xFF242424)
)

@Composable
fun YakshaganaLokaTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
