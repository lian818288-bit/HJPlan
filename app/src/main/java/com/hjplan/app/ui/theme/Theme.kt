package com.hjplan.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// iOS-inspired color palette
val HJBlue = Color(0xFF1A73E8)
val HJBlueDark = Color(0xFF1558B0)
val HJBackground = Color(0xFFF2F2F7)   // iOS system background
val HJSurface = Color(0xFFFFFFFF)
val HJSecondaryBg = Color(0xFFE5E5EA)  // iOS secondary background
val HJLabel = Color(0xFF1C1C1E)        // iOS label
val HJSecondaryLabel = Color(0xFF8E8E93)
val HJSeparator = Color(0xFFC6C6C8)
val HJGreen = Color(0xFF34C759)        // iOS green
val HJRed = Color(0xFFFF3B30)
val HJOrange = Color(0xFFFF9500)
val HJPurple = Color(0xFFAF52DE)

private val LightColorScheme = lightColorScheme(
    primary = HJBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE6F1FB),
    onPrimaryContainer = HJBlueDark,
    secondary = HJSecondaryLabel,
    onSecondary = Color.White,
    background = HJBackground,
    onBackground = HJLabel,
    surface = HJSurface,
    onSurface = HJLabel,
    surfaceVariant = HJSecondaryBg,
    onSurfaceVariant = HJSecondaryLabel,
    outline = HJSeparator,
    error = HJRed,
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4D9CF8),
    onPrimary = Color(0xFF003258),
    background = Color(0xFF1C1C1E),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF2C2C2E),
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF3A3A3C),
    onSurfaceVariant = Color(0xFFAEAEB2),
    outline = Color(0xFF48484A),
)

@Composable
fun HJPlanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
