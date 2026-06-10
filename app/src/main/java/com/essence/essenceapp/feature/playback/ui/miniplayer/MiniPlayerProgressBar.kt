package com.essence.essenceapp.feature.playback.ui.miniplayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.ui.theme.PureWhite

/**
 * Barra superior de progreso del MiniPlayer.
 *
 * Recibe un valor ya normalizado entre 0 y 1.
 */
@Composable
internal fun MiniPlayerProgressBar(
    progress: Float,
    accent: Color,
    accentGlow: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(2.dp)
            .background(PureWhite.copy(alpha = 0.06f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(2.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            accent.copy(alpha = 0.70f),
                            accent.copy(alpha = 0.92f),
                            accentGlow
                        )
                    )
                )
        )
    }
}