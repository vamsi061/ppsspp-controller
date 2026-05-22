package com.gamingcontroller.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val PSPBlue = Color(0xFF2196F3)
val PSPGreen = Color(0xFF4CAF50)
val PSPRed = Color(0xFFF44336)
val PSPPink = Color(0xFFE91E63)
val PSPOrange = Color(0xFFFF9800)
val PSPPurple = Color(0xFF9C27B0)
val PSPDarkBg = Color(0xFF0D0D0D)
val PSPSurface = Color(0xFF1A1A2E)
val PSPOverlayBg = Color(0x99000000)
val PSPSemiTransparent = Color(0x4D000000)
val PSPButtonDefault = Color(0x80424242)
val PSPSilver = Color(0xFFB0B0B0)

private val PPSSPPColorScheme = darkColorScheme(
    primary = PSPBlue,
    secondary = PSPPurple,
    tertiary = PSPGreen,
    background = PSPDarkBg,
    surface = PSPSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFE0E0E0)
)

@Composable
fun GamingControllerTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = PPSSPPColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color(0xFF0D0D0D).toArgb()
            window.navigationBarColor = Color(0xFF0D0D0D).toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
