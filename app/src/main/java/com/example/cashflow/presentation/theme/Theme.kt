@file:Suppress("DEPRECATION")

package com.example.cashflow.presentation.theme

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

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4CAF50),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = Color(0xFF1B5E20),

    secondary = Color(0xFF009688),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB2DFDB),
    onSecondaryContainer = Color(0xFF004D40),

    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF212121),

    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF212121),

    surfaceVariant = Color(0xFFE0E0E0),
    onSurfaceVariant = Color(0xFF616161),

    error = Color(0xFFF44336),
    onError = Color.White,
    errorContainer = Color(0xFFFDECEA),
    onErrorContainer = Color(0xFFB71C1C),

    outline = Color(0xFF9E9E9E),
)
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF81C784),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF388E3C),
    onPrimaryContainer = Color(0xFFC8E6C9),

    secondary = Color(0xFF4DB6AC),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF00796B),
    onSecondaryContainer = Color(0xFFB2DFDB),

    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),

    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),

    surfaceVariant = Color(0xFF424242),
    onSurfaceVariant = Color(0xFFBDBDBD),

    error = Color(0xFFEF9A9A),
    onError = Color.Black,
    errorContainer = Color(0xFFB71C1C),
    onErrorContainer = Color(0xFFFDECEA),

    outline = Color(0xFF757575),
)

@Composable
fun CashFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme()/* Dynamic color is available on Android 12+ */,
    dynamicColor: Boolean = false,
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
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
