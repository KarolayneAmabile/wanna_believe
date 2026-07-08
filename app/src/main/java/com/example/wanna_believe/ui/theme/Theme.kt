package com.example.wanna_believe.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = SageGreen,
    onPrimary = Color.Black,
    primaryContainer = ForestDeep,
    onPrimaryContainer = TextPrimaryLight,
    secondary = Terracotta,
    onSecondary = Color.White,
    background = DeepSpace,
    onBackground = TextPrimaryLight,
    surface = SurfaceDark,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFF242B26),
    onSurfaceVariant = Color(0xFFC2C9C0),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldHigh,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD3E8D3),
    onPrimaryContainer = ForestDeep,
    secondary = Terracotta,
    onSecondary = Color.White,
    background = OffWhite,
    onBackground = TextPrimaryDark,
    surface = Color.White,
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFFDEE5DA),
    onSurfaceVariant = TextSecondaryDark,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun WannaBelieveTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            
            val insetsController = WindowCompat.getInsetsController(window, view)
            // Se for tema claro, ícones escuros. Se escuro, ícones claros.
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = WannaBelieveTypography,
        content = content
    )
}
