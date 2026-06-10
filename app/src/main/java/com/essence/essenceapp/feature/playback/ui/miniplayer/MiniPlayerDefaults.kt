package com.essence.essenceapp.feature.playback.ui.miniplayer

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose

internal object MiniPlayerDefaults {
    const val PulseCycleMs = 500
    const val LongPressAnimationMs = 180
    const val LongPressHintDelayMs = 200L
    const val MarqueeVelocityDpPerSec = 30

    val ContainerShape = RoundedCornerShape(24.dp)
    val CoverShape = RoundedCornerShape(14.dp)
    val CoverGlowShape = RoundedCornerShape(18.dp)
    val MarqueeGap = 24.dp

    fun colors(
        isPlaying: Boolean,
        isBuffering: Boolean
    ): MiniPlayerColors {
        val accent = when {
            isBuffering -> MutedTeal
            isPlaying -> SoftRose
            else -> PureWhite.copy(alpha = 0.45f)
        }

        val accentGlow = when {
            isBuffering -> MutedTeal
            isPlaying -> SoftRose
            else -> PureWhite.copy(alpha = 0.55f)
        }

        return MiniPlayerColors(
            accent = accent,
            accentGlow = accentGlow
        )
    }
}

internal data class MiniPlayerColors(
    val accent: Color,
    val accentGlow: Color
)