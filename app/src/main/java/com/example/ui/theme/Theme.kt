package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.model.AppThemeMode
import com.example.model.ThemeDisplayMode

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

// Dark Theme Variants
private val DarkGeometricBalanceColorScheme = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    onPrimary = Color(0xFF381E72),
    primaryContainer = Color(0xFF4F378B),
    onPrimaryContainer = Color(0xFFEADDFF),
    background = Color(0xFF141218),
    onBackground = Color(0xFFE6E0E9),
    surface = Color(0xFF141218),
    onSurface = Color(0xFFE6E0E9),
    surfaceVariant = Color(0xFF2B2930),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454F)
)

private val DarkPurpleColorScheme = DarkGeometricBalanceColorScheme

private val DarkGoldColorScheme = darkColorScheme(
    primary = Color(0xFFFFD54F),
    onPrimary = Color(0xFF3E2723),
    primaryContainer = Color(0xFF5D4037),
    onPrimaryContainer = Color(0xFFFFECB3),
    background = Color(0xFF181511),
    onBackground = Color(0xFFEDE0D4),
    surface = Color(0xFF181511),
    onSurface = Color(0xFFEDE0D4),
    surfaceVariant = Color(0xFF2E2720),
    onSurfaceVariant = Color(0xFFD7CCC8),
    outline = Color(0xFFA1887F),
    outlineVariant = Color(0xFF4E342E)
)

@Composable
fun SmallStoreTheme(
    themeMode: AppThemeMode = AppThemeMode.NEUTRAL,
    displayMode: ThemeDisplayMode = ThemeDisplayMode.LIGHT,
    content: @Composable () -> Unit
) {
    val isDark = when (displayMode) {
        ThemeDisplayMode.LIGHT -> false
        ThemeDisplayMode.DARK -> true
        ThemeDisplayMode.AUTO -> isSystemInDarkTheme()
    }
    val colorScheme = if (isDark) {
        when (themeMode) {
            AppThemeMode.NEUTRAL -> DarkGeometricBalanceColorScheme
            AppThemeMode.PURPLE -> DarkPurpleColorScheme
            AppThemeMode.GOLD -> DarkGoldColorScheme
        }
    } else {
        when (themeMode) {
            AppThemeMode.NEUTRAL -> GeometricBalanceColorScheme
            AppThemeMode.PURPLE -> PurpleColorScheme
            AppThemeMode.GOLD -> GoldColorScheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Keep backwards-compatibility for existing references
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val displayMode = if (darkTheme) ThemeDisplayMode.DARK else ThemeDisplayMode.LIGHT
    SmallStoreTheme(themeMode = AppThemeMode.NEUTRAL, displayMode = displayMode, content = content)
}
