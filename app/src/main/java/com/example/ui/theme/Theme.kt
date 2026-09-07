package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppThemeMode {
    NEUTRAL,
    PURPLE,
    GOLD
}

// Geometric Balance Color Scheme (Clean #F7F9FC background, #6750A4 accent, #1A1C1E text)
private val GeometricBalanceColorScheme = lightColorScheme(
    primary = GeoPrimary,
    onPrimary = GeoOnPrimary,
    primaryContainer = GeoPrimaryContainer,
    onPrimaryContainer = GeoOnPrimaryContainer,
    background = GeoBackground,
    onBackground = GeoOnBackground,
    surface = GeoSurface,
    onSurface = GeoOnSurface,
    surfaceVariant = GeoSurfaceVariant,
    onSurfaceVariant = GeoOnSurfaceVariant,
    outline = GeoOutline,
    outlineVariant = GeoOutlineVariant
)

private val PurpleColorScheme = GeometricBalanceColorScheme

private val GoldColorScheme = lightColorScheme(
    primary = GoldAccentPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFF8E1),
    onPrimaryContainer = Color(0xFF5D4002),
    background = GoldAccentBackground,
    onBackground = GeoOnBackground,
    surface = GoldAccentSurface,
    onSurface = GeoOnSurface,
    surfaceVariant = Color(0xFFF7F4EC),
    onSurfaceVariant = Color(0xFF73777F),
    outline = GoldAccentOutline,
    outlineVariant = Color(0xFFEFE9DC)
)

@Composable
fun SmallStoreTheme(
    themeMode: AppThemeMode = AppThemeMode.NEUTRAL,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        AppThemeMode.NEUTRAL -> GeometricBalanceColorScheme
        AppThemeMode.PURPLE -> PurpleColorScheme
        AppThemeMode.GOLD -> GoldColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Keep backwards-compatibility for existing tests/references
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    SmallStoreTheme(themeMode = AppThemeMode.NEUTRAL, content = content)
}
