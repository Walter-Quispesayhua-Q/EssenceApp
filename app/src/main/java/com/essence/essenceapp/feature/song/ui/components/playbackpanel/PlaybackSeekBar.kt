package com.essence.essenceapp.feature.song.ui.components.playbackpanel

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import kotlin.math.abs
import kotlinx.coroutines.delay

/**
 * Barra de progreso interactiva.
 *
 * Controla solo el seek visual y envia la nueva posicion hacia afuera.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PlaybackSeekBar(
    positionMs: Long,
    durationMs: Long,
    isBuffering: Boolean,
    sliderColor: Color,
    sliderGlow: Color,
    onSeekTo: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val hasDuration = durationMs > 0L
    val safeDurationMs = durationMs.coerceAtLeast(0L)
    val safePositionMs = if (hasDuration) {
        positionMs.coerceIn(0L, safeDurationMs)
    } else {
        0L
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isDragging by interactionSource.collectIsDraggedAsState()
    val haptic = LocalHapticFeedback.current
    var draggedPositionMs by remember { mutableStateOf<Float?>(null) }
    var pendingSeekPositionMs by remember { mutableStateOf<Float?>(null) }

    val animatedPositionMs by animateFloatAsState(
        targetValue = safePositionMs.toFloat(),
        animationSpec = tween(
            durationMillis = PlaybackPanelDefaults.SeekProgressAnimationMs,
            easing = LinearEasing
        ),
        label = "panel_seek_position"
    )

    val visualPositionMs = when {
        !hasDuration -> 0f
        isDragging -> draggedPositionMs ?: animatedPositionMs
        pendingSeekPositionMs != null -> pendingSeekPositionMs ?: animatedPositionMs
        else -> animatedPositionMs
    }.coerceIn(0f, safeDurationMs.toFloat().coerceAtLeast(1f))

    val visualPositionLong = visualPositionMs.toLong()
    val visualFraction = if (hasDuration) {
        (visualPositionMs / safeDurationMs.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is DragInteraction.Start ->
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)

                is DragInteraction.Stop,
                is DragInteraction.Cancel ->
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }

            if (interaction is DragInteraction.Cancel) {
                draggedPositionMs = null
                pendingSeekPositionMs = null
            }
        }
    }

    LaunchedEffect(pendingSeekPositionMs, safePositionMs) {
        val pending = pendingSeekPositionMs ?: return@LaunchedEffect
        if (abs(safePositionMs.toFloat() - pending) <= SEEK_SETTLE_TOLERANCE_MS) {
            pendingSeekPositionMs = null
        }
    }

    LaunchedEffect(pendingSeekPositionMs) {
        val pending = pendingSeekPositionMs ?: return@LaunchedEffect
        delay(PlaybackPanelDefaults.SeekPendingHoldMs.toLong())
        if (pendingSeekPositionMs == pending) {
            pendingSeekPositionMs = null
        }
    }

    val thumbOuterSize by animateDpAsState(
        targetValue = if (isDragging) 28.dp else 20.dp,
        animationSpec = tween(
            durationMillis = PlaybackPanelDefaults.ThumbAnimationMs,
            easing = FastOutSlowInEasing
        ),
        label = "panel_thumb_outer"
    )

    val thumbInnerSize by animateDpAsState(
        targetValue = if (isDragging) 18.dp else 14.dp,
        animationSpec = tween(
            durationMillis = PlaybackPanelDefaults.ThumbAnimationMs,
            easing = FastOutSlowInEasing
        ),
        label = "panel_thumb_inner"
    )

    androidx.compose.foundation.layout.Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        SeekPreviewPill(
            visible = isDragging,
            positionMs = visualPositionLong,
            accent = sliderColor
        )

        Slider(
            value = visualPositionMs,
            onValueChange = { value ->
                if (hasDuration) {
                    draggedPositionMs = value.coerceIn(0f, safeDurationMs.toFloat())
                    pendingSeekPositionMs = null
                }
            },
            onValueChangeFinished = {
                if (hasDuration) {
                    val targetPosition = (draggedPositionMs ?: visualPositionMs)
                        .coerceIn(0f, safeDurationMs.toFloat())
                        .toLong()
                    pendingSeekPositionMs = targetPosition.toFloat()
                    draggedPositionMs = null
                    onSeekTo(targetPosition)
                }
            },
            valueRange = 0f..(safeDurationMs.takeIf { hasDuration } ?: 1L).toFloat(),
            interactionSource = interactionSource,
            colors = SliderDefaults.colors(
                thumbColor = Color.Transparent,
                activeTrackColor = Color.Transparent,
                inactiveTrackColor = Color.Transparent
            ),
            thumb = {
                Box(
                    modifier = Modifier.size(30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(thumbOuterSize)
                            .background(
                                sliderColor.copy(alpha = if (isDragging) 0.35f else 0.22f),
                                CircleShape
                            )
                    )

                    Box(
                        modifier = Modifier
                            .size(thumbInnerSize)
                            .background(PureWhite, CircleShape)
                    )
                }
            },
            track = { _ ->
                PlaybackSeekTrack(
                    fraction = visualFraction,
                    isBuffering = isBuffering,
                    sliderColor = sliderColor,
                    sliderGlow = sliderGlow
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        PlaybackTimeRow(
            positionMs = visualPositionLong,
            durationMs = safeDurationMs
        )
    }
}

@Composable
private fun SeekPreviewPill(
    visible: Boolean,
    positionMs: Long,
    accent: Color
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(PlaybackPanelDefaults.SeekPreviewFadeMs)),
        exit = fadeOut(animationSpec = tween(PlaybackPanelDefaults.SeekPreviewFadeMs))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = accent.copy(alpha = 0.16f),
                border = BorderStroke(1.dp, accent.copy(alpha = 0.42f)),
                shadowElevation = 6.dp
            ) {
                Text(
                    text = formatPlaybackTime(positionMs),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    color = accent
                )
            }
        }
    }
}

@Composable
private fun PlaybackSeekTrack(
    fraction: Float,
    isBuffering: Boolean,
    sliderColor: Color,
    sliderGlow: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(PureWhite.copy(alpha = 0.09f))
    ) {
        if (isBuffering) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(MutedTeal.copy(alpha = 0.35f))
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(rememberSliderBufferShimmer())
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                sliderColor.copy(alpha = 0.70f),
                                sliderColor.copy(alpha = 0.92f),
                                sliderGlow.copy(alpha = 1f)
                            )
                        )
                    )
            )
        }
    }
}

@Composable
private fun PlaybackTimeRow(
    positionMs: Long,
    durationMs: Long
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = formatPlaybackTime(positionMs),
            style = MaterialTheme.typography.labelSmall,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium,
            color = PureWhite.copy(alpha = 0.62f)
        )

        Text(
            text = formatPlaybackTime(durationMs),
            style = MaterialTheme.typography.labelSmall,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium,
            color = PureWhite.copy(alpha = 0.42f)
        )
    }
}

@Composable
private fun rememberSliderBufferShimmer(): Brush {
    val transition = rememberInfiniteTransition(label = "panel_buffer_shimmer")

    val translate by transition.animateFloat(
        initialValue = -240f,
        targetValue = 1400f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = PlaybackPanelDefaults.BufferShimmerMs,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "panel_buffer_shimmer_value"
    )

    return Brush.horizontalGradient(
        colors = listOf(
            Color.Transparent,
            MutedTeal.copy(alpha = 0.45f),
            MutedTeal.copy(alpha = 0.75f),
            MutedTeal.copy(alpha = 0.45f),
            Color.Transparent
        ),
        startX = translate - 140f,
        endX = translate + 140f
    )
}

private const val SEEK_SETTLE_TOLERANCE_MS = 750f
