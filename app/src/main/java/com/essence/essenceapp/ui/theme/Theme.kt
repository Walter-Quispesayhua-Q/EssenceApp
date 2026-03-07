package com.essence.essenceapp.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    background = MidnightBlack,
    surface = GraphiteSurface,

    primary = SoftRose,
    onPrimary = MidnightBlack,

    secondary = MutedTeal,
    onSecondary = MidnightBlack,

    tertiary = LuxeGold,
    onTertiary = MidnightBlack,

    onBackground = PureWhite,
    onSurface = PureWhite,
)

private val LightColorScheme = lightColorScheme(
    /*
    TODO: Implementar paleta Light Mode en el futuro.
    Por ahora la app solo usa Dark Mode.
    */
)

@Composable
fun EssenceAppTheme(
    darkTheme: Boolean = true,
    // Dynamic color is available on Android 12+
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
        shapes = EssenceShapes,
        content = content
    )
}