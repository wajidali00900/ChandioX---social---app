package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = FbBlue,
    onPrimary = FbWhite,
    primaryContainer = FbBlueDark,
    onPrimaryContainer = FbWhite,
    secondary = FbBlueSecondary,
    onSecondary = FbWhite,
    background = FbBgDark,
    onBackground = FbTextPrimaryDark,
    surface = FbCardBgDark,
    onSurface = FbTextPrimaryDark,
    surfaceVariant = FbDividerDark,
    onSurfaceVariant = FbTextSecondaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = FbBlue,
    onPrimary = FbWhite,
    primaryContainer = FbBlueLight,
    onPrimaryContainer = FbBlueDark,
    secondary = FbBlueSecondary,
    onSecondary = FbWhite,
    background = FbBgLight,
    onBackground = FbTextPrimary,
    surface = FbCardBg,
    onSurface = FbTextPrimary,
    surfaceVariant = FbSurfaceVariant,
    onSurfaceVariant = FbTextSecondary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Keep consistent Facebook branding by default
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
