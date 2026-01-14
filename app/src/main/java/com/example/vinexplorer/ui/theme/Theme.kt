package com.example.vinexplorer.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Blue80,
    onPrimary = Blue10,
    primaryContainer = Blue30,
    onPrimaryContainer = Blue90,

    secondary = Gray80,
    onSecondary = Gray10,
    secondaryContainer = Gray30,
    onSecondaryContainer = Gray90,

    tertiary = Orange80,
    onTertiary = Orange10,
    tertiaryContainer = Orange30,
    onTertiaryContainer = Orange90,

    error = Red80,
    onError = Red10,
    errorContainer = Red30,
    onErrorContainer = Red90,

    background = SurfaceDark,
    onBackground = Color(0xFFE3E2E6),
    surface = SurfaceDark,
    onSurface = Color(0xFFE3E2E6),
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Color(0xFFC4C6CF),

    outline = Color(0xFF8E9099),
    outlineVariant = Color(0xFF44474E)
)

private val LightColorScheme = lightColorScheme(
    primary = Blue40,
    onPrimary = Color.White,
    primaryContainer = Blue90,
    onPrimaryContainer = Blue10,

    secondary = Gray40,
    onSecondary = Color.White,
    secondaryContainer = Gray90,
    onSecondaryContainer = Gray10,

    tertiary = Orange40,
    onTertiary = Color.White,
    tertiaryContainer = Orange90,
    onTertiaryContainer = Orange10,

    error = Red40,
    onError = Color.White,
    errorContainer = Red90,
    onErrorContainer = Red10,

    background = SurfaceLight,
    onBackground = Color(0xFF1A1C1E),
    surface = SurfaceLight,
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Color(0xFF44474E),

    outline = Color(0xFF74777F),
    outlineVariant = Color(0xFFC4C6CF)
)

@Composable
fun VinExplorerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

