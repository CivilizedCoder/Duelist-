package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MedievalDarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = Color.Black,
    primaryContainer = SteelMedium,
    onPrimaryContainer = GoldLight,
    secondary = BronzeAccent,
    onSecondary = Color.White,
    tertiary = CrimsonRed,
    onTertiary = Color.White,
    background = BackgroundDark,
    onBackground = Color(0xFFE2E4E9),
    surface = SurfaceDark,
    onSurface = Color(0xFFE2E4E9),
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Color(0xFFC4C7D0),
    error = CrimsonBright,
    onError = Color.White
)

@Composable
fun IronAndSteelTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MedievalDarkColorScheme,
        typography = Typography,
        content = content
    )
}

