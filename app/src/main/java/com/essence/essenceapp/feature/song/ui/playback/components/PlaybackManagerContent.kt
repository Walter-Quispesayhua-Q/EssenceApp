package com.essence.essenceapp.feature.song.ui.playback.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.song.ui.components.GlassIsland
import com.essence.essenceapp.feature.song.ui.playback.PlaybackAction
import com.essence.essenceapp.feature.song.ui.playback.PlaybackRepeatMode
import com.essence.essenceapp.feature.song.ui.playback.PlaybackUiState
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.LuxeGold
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose
import kotlin.math.max

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun PlaybackManagerContent(
    modifier: Modifier = Modifier,
    state: PlaybackUiState,
    onAction: (PlaybackAction) -> Unit,
    songTitle: String? = null,
    artistName: String? = null,
    isLiked: Boolean = false,
    isLikeSubmitting: Boolean = false,
    onToggleLike: (() -> Unit)? = null,
    showMetaHeader: Boolean = true
) {
    val durationMs = max(state.durationMs, 1L)
    val positionMs = state.positionMs.coerceIn(0L, durationMs)

    val accent = when {
        state.isBuffering -> MutedTeal
        state.isPlaying -> SoftRose
        else -> PureWhite.copy(alpha = 0.45f)
    }

    val accentStrength = when {
        state.isBuffering -> 0.10f
        state.isPlaying -> 0.08f
        else -> 0.04f
    }

    val sliderColor = when {
        state.isBuffering -> MutedTeal
        state.isPlaying -> SoftRose
        else -> LuxeGold
    }
    val sliderGlow = if (state.isBuffering) SoftRose else LuxeGold

    val isRepeatOne = state.repeatMode == PlaybackRepeatMode.One

    GlassIsland(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        accent = accent,
        isPulsing = state.isPlaying && !state.isBuffering,
        accentAlpha = accentStrength,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val compactMode = maxWidth < 335.dp

            val repeatButtonSize = if (compactMode) 46.dp else 56.dp
            val repeatIconSize = if (compactMode) 20.dp else 24.dp

            val transportButtonSize = if (compactMode) 50.dp else 60.dp
            val transportIconSize = if (compactMode) 22.dp else 26.dp

            val playButtonSize = if (compactMode) 72.dp else 88.dp
            val playIconSize = if (compactMode) 32.dp else 38.dp

            val likeButtonSize = if (compactMode) 46.dp else 56.dp
            val likeIconSize = if (compactMode) 20.dp else 24.dp

            val controlsSpacing = if (compactMode) 4.dp else 8.dp
            val centerGroupMaxWidth = if (compactMode) 240.dp else 300.dp

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (showMetaHeader) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = accent.copy(alpha = 0.16f),
                            border = BorderStroke(1.dp, accent.copy(alpha = 0.32f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = "Reproduciendo",
                                tint = accent,
                                modifier = Modifier
                                    .padding(7.dp)
                                    .size(18.dp)
                            )
                        }

                        PlaybackStateIndicator(
                            isBuffering = state.isBuffering,
                            isPlaying = state.isPlaying
                        )
                    }
                }

                val interactionSource = remember { MutableInteractionSource() }
                val isDragging by interactionSource.collectIsDraggedAsState()
                val haptic = LocalHapticFeedback.current

                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect { interaction ->
                        when (interaction) {
                            is DragInteraction.Start ->
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            is DragInteraction.Stop,
                            is DragInteraction.Cancel ->
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    }
                }

                val thumbOuterSize by animateDpAsState(
                    targetValue = if (isDragging) 28.dp else 20.dp,
                    animationSpec = tween(180, easing = FastOutSlowInEasing),
                    label = "thumb_outer"
                )
                val thumbInnerSize by animateDpAsState(
                    targetValue = if (isDragging) 18.dp else 14.dp,
                    animationSpec = tween(180, easing = FastOutSlowInEasing),
                    label = "thumb_inner"
                )

                SeekPreviewPill(
                    visible = isDragging,
                    positionMs = positionMs,
                    accent = sliderColor
                )

                Slider(
                    value = positionMs.toFloat(),
                    onValueChange = { onAction(PlaybackAction.SeekTo(it.toLong())) },
                    valueRange = 0f..durationMs.toFloat(),
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
                        val fraction = if (durationMs > 0) positionMs.toFloat() / durationMs.toFloat() else 0f

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(PureWhite.copy(alpha = 0.09f))
                        ) {
                            if (state.isBuffering) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fraction.coerceIn(0f, 1f))
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
                                        .fillMaxWidth(fraction.coerceIn(0f, 1f))
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
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatTime(positionMs),
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium,
                        color = PureWhite.copy(alpha = 0.62f)
                    )
                    Text(
                        text = formatTime(durationMs),
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium,
                        color = PureWhite.copy(alpha = 0.42f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        RepeatToggleButton(
                            isRepeat = isRepeatOne,
                            onClick = { onAction(PlaybackAction.ToggleRepeat) },
                            buttonSize = repeatButtonSize,
                            iconSize = repeatIconSize
                        )
                    }

                    Row(
                        modifier = Modifier.widthIn(max = centerGroupMaxWidth),
                        horizontalArrangement = Arrangement.spacedBy(
                            controlsSpacing,
                            Alignment.CenterHorizontally
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TransportButton(
                            icon = Icons.Default.SkipPrevious,
                            description = "Anterior",
                            enabled = state.canGoPrevious,
                            onClick = { onAction(PlaybackAction.Previous) },
                            buttonSize = transportButtonSize,
                            iconSize = transportIconSize
                        )

                        PlayPauseButton(
                            isPlaying = state.isPlaying,
                            isBuffering = state.isBuffering,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onAction(
                                    if (state.isPlaying) PlaybackAction.Pause
                                    else PlaybackAction.Play
                                )
                            },
                            buttonSize = playButtonSize,
                            iconSize = playIconSize
                        )

                        TransportButton(
                            icon = Icons.Default.SkipNext,
                            description = "Siguiente",
                            enabled = state.canGoNext,
                            onClick = { onAction(PlaybackAction.Next) },
                            buttonSize = transportButtonSize,
                            iconSize = transportIconSize
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
                                buttonSize = likeButtonSize,
                                iconSize = likeIconSize
                            )
                        } else {
                            Spacer(modifier = Modifier.size(likeButtonSize))
                        }
                    }
                }

                state.errorMessage?.takeIf { it.isNotBlank() }?.let { msg ->
                    PlaybackErrorRow(
                        message = msg,
                        onRetry = { onAction(PlaybackAction.Play) }
                    )
                }
            }
        }
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
        enter = fadeIn(animationSpec = tween(140)),
        exit = fadeOut(animationSpec = tween(140))
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
                    text = formatTime(positionMs),
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
private fun rememberSliderBufferShimmer(): Brush {
    val transition = rememberInfiniteTransition(label = "buffer_shimmer")
    val translate by transition.animateFloat(
        initialValue = -240f,
        targetValue = 1400f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "buffer_shimmer_value"
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

@Composable
private fun PlaybackErrorRow(
    message: String,
    onRetry: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = SoftRose.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, SoftRose.copy(alpha = 0.24f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = SoftRose,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = message,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelMedium,
                color = SoftRose.copy(alpha = 0.92f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            IconButton(
                onClick = onRetry,
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reintentar",
                    tint = SoftRose,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun PlaybackStateIndicator(
    isBuffering: Boolean,
    isPlaying: Boolean
) {
    if (!isBuffering && !isPlaying) return

    if (isBuffering) {
        CircularProgressIndicator(
            modifier = Modifier.size(12.dp),
            strokeWidth = 1.6.dp,
            color = MutedTeal
        )
    } else {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    color = SoftRose,
                    shape = CircleShape
                )
        )
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
        val transition = rememberInfiniteTransition(label = "play_ring")
        ringScale = transition.animateFloat(
            initialValue = 1f,
            targetValue = 1.10f,
            animationSpec = infiniteRepeatable(
                animation = tween(500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "ring_scale"
        ).value
        ringAlpha = transition.animateFloat(
            initialValue = 0.20f,
            targetValue = 0.42f,
            animationSpec = infiniteRepeatable(
                animation = tween(500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "ring_alpha"
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
            IconButton(onClick = onClick, enabled = !isBuffering) {
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
                tint = if (enabled) PureWhite.copy(alpha = 0.88f) else PureWhite.copy(alpha = 0.22f),
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
        color = if (isLiked) SoftRose.copy(alpha = 0.14f) else PureWhite.copy(alpha = 0.05f),
        border = BorderStroke(
            1.dp,
            if (isLiked) SoftRose.copy(alpha = 0.34f)
            else PureWhite.copy(alpha = 0.08f)
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
    isRepeat: Boolean,
    onClick: () -> Unit,
    buttonSize: Dp,
    iconSize: Dp
) {
    Surface(
        modifier = Modifier.size(buttonSize),
        shape = CircleShape,
        color = if (isRepeat) LuxeGold.copy(alpha = 0.14f) else PureWhite.copy(alpha = 0.05f),
        border = BorderStroke(
            1.dp,
            if (isRepeat) LuxeGold.copy(alpha = 0.34f)
            else PureWhite.copy(alpha = 0.08f)
        )
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = if (isRepeat) Icons.Default.RepeatOne else Icons.Default.Repeat,
                contentDescription = if (isRepeat) "Desactivar repetir" else "Repetir cancion",
                tint = if (isRepeat) LuxeGold else PureWhite.copy(alpha = 0.52f),
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

@Preview(name = "Playback - Compact Playing", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun FullPreview() {
    EssenceAppTheme {
        PlaybackManagerContent(
            state = PlaybackUiState(
                isPlaying = true,
                positionMs = 125_000L,
                durationMs = 240_000L,
                repeatMode = PlaybackRepeatMode.One,
                canGoPrevious = true,
                canGoNext = true
            ),
            onAction = {},
            songTitle = "Titi Me Pregunto",
            artistName = "Bad Bunny",
            isLiked = true,
            onToggleLike = {}
        )
    }
}

@Preview(name = "Playback - Compact Paused", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PausedPreview() {
    EssenceAppTheme {
        PlaybackManagerContent(
            state = PlaybackUiState(
                isPlaying = false,
                positionMs = 45_000L,
                durationMs = 210_000L,
                repeatMode = PlaybackRepeatMode.Off,
                canGoPrevious = false,
                canGoNext = false
            ),
            onAction = {},
            songTitle = "Moscow Mule",
            artistName = "Bad Bunny",
            isLiked = false,
            onToggleLike = {}
        )
    }
}

@Preview(name = "Playback - Compact Buffering", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun BufferingPreview() {
    EssenceAppTheme {
        PlaybackManagerContent(
            state = PlaybackUiState(
                isPlaying = true,
                isBuffering = true,
                positionMs = 80_000L,
                durationMs = 200_000L,
                repeatMode = PlaybackRepeatMode.Off
            ),
            onAction = {},
            songTitle = "Everybody Wants To Rule The World",
            artistName = "Harry Styles",
            isLiked = false,
            onToggleLike = {}
        )
    }
}