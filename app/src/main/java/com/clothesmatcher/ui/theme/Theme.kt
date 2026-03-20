package com.clothesmatcher.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Black,
    onPrimary = White,
    secondary = Gray900,
    onSecondary = White,
    background = White,
    surface = White,
    onBackground = Black,
    onSurface = Black
)

@Composable
fun ClothesMatcherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Forcing a clean light theme as requested, ignoring darkTheme for now if we want "light with whites"
    content: @Composable () -> Unit
) {
    // We'll stick to LightColorScheme for that "clean white" look requested
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
