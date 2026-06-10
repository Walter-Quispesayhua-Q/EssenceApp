package com.essence.essenceapp.feature.song.ui.components.playbackpanel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.ui.theme.LuxeGold
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose

internal object PlaybackPanelDefaults {
    const val ThumbAnimationMs = 180
    const val SeekProgressAnimationMs = 520
    const val SeekPendingHoldMs = 900
    const val SeekPreviewFadeMs = 140
    const val BufferShimmerMs = 1400
    const val PlayRingPulseMs = 500

    fun colors(
        isPlaying: Boolean,
        isBuffering: Boolean
    ): PlaybackPanelColors {
        val accent = when {
            isBuffering -> MutedTeal
            isPlaying -> SoftRose
            else -> PureWhite.copy(alpha = 0.45f)
        }

        val accentStrength = when {
            isBuffering -> 0.10f
            isPlaying -> 0.08f
            else -> 0.04f
        }

        val sliderColor = when {
            isBuffering -> MutedTeal
            isPlaying -> SoftRose
            else -> LuxeGold
        }

        val sliderGlow = if (isBuffering) SoftRose else LuxeGold

        return PlaybackPanelColors(
            accent = accent,
            accentStrength = accentStrength,
            sliderColor = sliderColor,
            sliderGlow = sliderGlow
        )
    }

    fun sizes(compactMode: Boolean): PlaybackPanelSizes =
        PlaybackPanelSizes(
            repeatButtonSize = if (compactMode) 46.dp else 56.dp,
            repeatIconSize = if (compactMode) 20.dp else 24.dp,
            transportButtonSize = if (compactMode) 50.dp else 60.dp,
            transportIconSize = if (compactMode) 22.dp else 26.dp,
            playButtonSize = if (compactMode) 72.dp else 88.dp,
            playIconSize = if (compactMode) 32.dp else 38.dp,
            likeButtonSize = if (compactMode) 46.dp else 56.dp,
            likeIconSize = if (compactMode) 20.dp else 24.dp,
            controlsSpacing = if (compactMode) 4.dp else 8.dp,
            centerGroupMaxWidth = if (compactMode) 240.dp else 300.dp
        )
}

internal data class PlaybackPanelColors(
    val accent: Color,
    val accentStrength: Float,
    val sliderColor: Color,
    val sliderGlow: Color
)

internal data class PlaybackPanelSizes(
    val repeatButtonSize: Dp,
    val repeatIconSize: Dp,
    val transportButtonSize: Dp,
    val transportIconSize: Dp,
    val playButtonSize: Dp,
    val playIconSize: Dp,
    val likeButtonSize: Dp,
    val likeIconSize: Dp,
    val controlsSpacing: Dp,
    val centerGroupMaxWidth: Dp
)
