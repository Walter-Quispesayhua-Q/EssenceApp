package com.essence.essenceapp.feature.song.ui.components.playbackpanel

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.playback.domain.PlaybackRepeatMode
import com.essence.essenceapp.ui.theme.LuxeGold
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose

/**
 * Controles principales del panel: repeat, previous, play/pause, next y like.
 */
@Composable
internal fun PlaybackTransportControls(
    repeatMode: PlaybackRepeatMode,
    isPlaying: Boolean,
    isBuffering: Boolean,
    canGoPrevious: Boolean,
    canGoNext: Boolean,
    isLiked: Boolean,
    isLikeSubmitting: Boolean,
    onToggleRepeat: () -> Unit,
    onPrevious: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onToggleLike: (() -> Unit)?,
    sizes: PlaybackPanelSizes,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            RepeatToggleButton(
                repeatMode = repeatMode,
                onClick = onToggleRepeat,
                buttonSize = sizes.repeatButtonSize,
                iconSize = sizes.repeatIconSize
            )
        }

        Row(
            modifier = Modifier.widthIn(max = sizes.centerGroupMaxWidth),
            horizontalArrangement = Arrangement.spacedBy(
                sizes.controlsSpacing,
                Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TransportButton(
                icon = Icons.Default.SkipPrevious,
                description = "Anterior",
                enabled = canGoPrevious,
                onClick = onPrevious,
                buttonSize = sizes.transportButtonSize,
                iconSize = sizes.transportIconSize
            )

            PlayPauseButton(
                isPlaying = isPlaying,
                isBuffering = isBuffering,
                onClick = onPlayPause,
                buttonSize = sizes.playButtonSize,
                iconSize = sizes.playIconSize
            )

            TransportButton(
                icon = Icons.Default.SkipNext,
                description = "Siguiente",
                enabled = canGoNext,
                onClick = onNext,
                buttonSize = sizes.transportButtonSize,
                iconSize = sizes.transportIconSize
            )
        }

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterEnd
        ) {
            if (onToggleLike != null) {
                LikeButton(
                    isLiked = isLiked,
                    isSubmitting = isLikeSubmitting,
                    onClick = onToggleLike,
                    buttonSize = sizes.likeButtonSize,
                    iconSize = sizes.likeIconSize
                )
            } else {
                Spacer(modifier = Modifier.size(sizes.likeButtonSize))
            }
        }
    }
}

@Composable
private fun PlayPauseButton(
    isPlaying: Boolean,
    isBuffering: Boolean,
    onClick: () -> Unit,
    buttonSize: Dp,
    iconSize: Dp
) {
    val showRing = isPlaying && !isBuffering
    val accentColor = if (isBuffering) MutedTeal else SoftRose

    val ringScale: Float
    val ringAlpha: Float

    if (showRing) {
        val transition = rememberInfiniteTransition(label = "panel_play_ring")

        ringScale = transition.animateFloat(
            initialValue = 1f,
            targetValue = 1.10f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = PlaybackPanelDefaults.PlayRingPulseMs,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "panel_ring_scale"
        ).value

        ringAlpha = transition.animateFloat(
            initialValue = 0.20f,
            targetValue = 0.42f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = PlaybackPanelDefaults.PlayRingPulseMs,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "panel_ring_alpha"
        ).value
    } else {
        ringScale = 1f
        ringAlpha = 0f
    }

    Box(
        modifier = Modifier.size(buttonSize + 20.dp),
        contentAlignment = Alignment.Center
    ) {
        if (showRing) {
            Box(
                modifier = Modifier
                    .size(buttonSize)
                    .graphicsLayer {
                        scaleX = ringScale
                        scaleY = ringScale
                    }
                    .background(
                        accentColor.copy(alpha = ringAlpha),
                        CircleShape
                    )
            )
        }

        Surface(
            modifier = Modifier.size(buttonSize),
            shape = CircleShape,
            color = accentColor,
            shadowElevation = 12.dp
        ) {
            IconButton(onClick = onClick) {
                if (isBuffering) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(iconSize),
                        strokeWidth = 2.5.dp,
                        color = PureWhite
                    )
                } else {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                        tint = PureWhite,
                        modifier = Modifier.size(iconSize)
                    )
                }
            }
        }
    }
}

@Composable
private fun TransportButton(
    icon: ImageVector,
    description: String,
    enabled: Boolean,
    onClick: () -> Unit,
    buttonSize: Dp,
    iconSize: Dp
) {
    Surface(
        modifier = Modifier.size(buttonSize),
        shape = CircleShape,
        color = Color.Transparent
    ) {
        IconButton(
            onClick = onClick,
            enabled = enabled
        ) {
            Icon(
                imageVector = icon,
                contentDescription = description,
                tint = if (enabled) {
                    PureWhite.copy(alpha = 0.88f)
                } else {
                    PureWhite.copy(alpha = 0.22f)
                },
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

@Composable
private fun LikeButton(
    isLiked: Boolean,
    isSubmitting: Boolean,
    onClick: () -> Unit,
    buttonSize: Dp,
    iconSize: Dp
) {
    Surface(
        modifier = Modifier.size(buttonSize),
        shape = CircleShape,
        color = if (isLiked) {
            SoftRose.copy(alpha = 0.14f)
        } else {
            PureWhite.copy(alpha = 0.05f)
        },
        border = BorderStroke(
            1.dp,
            if (isLiked) {
                SoftRose.copy(alpha = 0.34f)
            } else {
                PureWhite.copy(alpha = 0.08f)
            }
        )
    ) {
        IconButton(
            onClick = onClick,
            enabled = !isSubmitting
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(iconSize),
                    strokeWidth = 2.dp,
                    color = SoftRose
                )
            } else {
                Icon(
                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isLiked) "Quitar like" else "Dar like",
                    tint = if (isLiked) SoftRose else PureWhite.copy(alpha = 0.52f),
                    modifier = Modifier.size(iconSize)
                )
            }
        }
    }
}

@Composable
private fun RepeatToggleButton(
    repeatMode: PlaybackRepeatMode,
    onClick: () -> Unit,
    buttonSize: Dp,
    iconSize: Dp
) {
    val isActive = repeatMode != PlaybackRepeatMode.OFF
    val isRepeatOne = repeatMode == PlaybackRepeatMode.ONE

    Surface(
        modifier = Modifier.size(buttonSize),
        shape = CircleShape,
        color = if (isActive) {
            LuxeGold.copy(alpha = 0.14f)
        } else {
            PureWhite.copy(alpha = 0.05f)
        },
        border = BorderStroke(
            1.dp,
            if (isActive) {
                LuxeGold.copy(alpha = 0.34f)
            } else {
                PureWhite.copy(alpha = 0.08f)
            }
        )
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = if (isRepeatOne) Icons.Default.RepeatOne else Icons.Default.Repeat,
                contentDescription = when (repeatMode) {
                    PlaybackRepeatMode.OFF -> "Activar repetir"
                    PlaybackRepeatMode.ONE -> "Repetir cancion"
                    PlaybackRepeatMode.ALL -> "Repetir cola"
                },
                tint = if (isActive) LuxeGold else PureWhite.copy(alpha = 0.52f),
                modifier = Modifier.size(iconSize)
            )
        }
    }
}