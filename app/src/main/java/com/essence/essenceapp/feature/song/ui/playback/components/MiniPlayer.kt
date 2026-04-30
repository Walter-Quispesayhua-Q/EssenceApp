package com.essence.essenceapp.feature.song.ui.playback.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.essence.essenceapp.core.network.resolveImageUrl
import com.essence.essenceapp.feature.song.ui.playback.PlaybackRepeatMode
import com.essence.essenceapp.feature.song.ui.playback.PlaybackUiState
import com.essence.essenceapp.feature.song.ui.playback.engine.AudioOutputType
import com.essence.essenceapp.feature.song.ui.playback.model.NowPlayingInfo
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose

private const val PULSE_CYCLE_MS = 500
private const val MARQUEE_VELOCITY_DP_PER_SEC = 30

@Composable
fun MiniPlayer(
    nowPlaying: NowPlayingInfo,
    playback: PlaybackUiState,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onDismiss: () -> Unit,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (playback.durationMs > 0L) {
        (playback.positionMs.toFloat() / playback.durationMs.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    val imageUrl = resolveImageUrl(nowPlaying.imageKey)
    val isActive = playback.isPlaying && !playback.isBuffering

    val accent = when {
        playback.isBuffering -> MutedTeal
        playback.isPlaying -> SoftRose
        else -> PureWhite.copy(alpha = 0.45f)
    }
    val accentGlow = when {
        playback.isBuffering -> MutedTeal
        playback.isPlaying -> SoftRose
        else -> PureWhite.copy(alpha = 0.55f)
    }

    val pulseScale = rememberMiniPulse(isActive = isActive)

    val isLongPressing = remember { mutableStateOf(false) }
    val longPressScale by animateFloatAsState(
        targetValue = if (isLongPressing.value) 0.94f else 1f,
        animationSpec = tween(180, easing = FastOutSlowInEasing),
        label = "long_press_scale"
    )
    val longPressAlpha by animateFloatAsState(
        targetValue = if (isLongPressing.value) 0.78f else 1f,
        animationSpec = tween(180, easing = FastOutSlowInEasing),
        label = "long_press_alpha"
    )
    val haptic = LocalHapticFeedback.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = pulseScale * longPressScale
                scaleY = pulseScale * longPressScale
                alpha = longPressAlpha
            }
            .clip(RoundedCornerShape(24.dp))
            .pointerInput(onDismiss, onTap) {
                detectTapGestures(
                    onPress = {
                        coroutineScope {
                            val visualHintJob = launch {
                                delay(200)
                                isLongPressing.value = true
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                            try {
                                tryAwaitRelease()
                            } finally {
                                visualHintJob.cancel()
                                isLongPressing.value = false
                            }
                        }
                    },
                    onTap = { onTap() },
                    onLongPress = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onDismiss()
                    }
                )
            },
        shape = RoundedCornerShape(24.dp),
        color = GraphiteSurface.copy(alpha = 0.90f),
        border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.07f)),
        shadowElevation = 12.dp
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                accent.copy(alpha = 0.10f),
                                Color.Transparent,
                                accent.copy(alpha = 0.04f)
                            )
                        )
                    )
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                TopProgressBar(
                    progress = progress,
                    accent = accent,
                    accentGlow = accentGlow
                )

                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, top = 8.dp, bottom = 10.dp)
                ) {
                    val compactMode = maxWidth < 360.dp

                    val coverSize = if (compactMode) 42.dp else 48.dp
                    val controlButtonSize = if (compactMode) 32.dp else 36.dp
                    val controlIconSize = if (compactMode) 20.dp else 22.dp
                    val playButtonSize = if (compactMode) 38.dp else 42.dp
                    val playIconSize = if (compactMode) 20.dp else 22.dp
                    val spacing = if (compactMode) 4.dp else 8.dp
                    val controlsWidth = if (compactMode) 118.dp else 138.dp

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PulsingCover(
                            title = nowPlaying.title,
                            imageUrl = imageUrl,
                            coverSize = coverSize,
                            isActive = isActive
                        )

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            MarqueeText(
                                text = nowPlaying.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = PureWhite
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = playback.audioOutput.icon,
                                    contentDescription = playback.audioOutput.label,
                                    tint = MutedTeal.copy(alpha = 0.72f),
                                    modifier = Modifier.size(11.dp)
                                )
                                Text(
                                    text = nowPlaying.artistName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PureWhite.copy(alpha = 0.56f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.widthIn(max = controlsWidth),
                            horizontalArrangement = Arrangement.spacedBy(spacing),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MiniTransportButton(
                                icon = Icons.Default.SkipPrevious,
                                description = "Anterior",
                                enabled = playback.canGoPrevious,
                                onClick = onPrevious,
                                buttonSize = controlButtonSize,
                                iconSize = controlIconSize
                            )

                            MiniPlayPauseButton(
                                isPlaying = playback.isPlaying,
                                isBuffering = playback.isBuffering,
                                onClick = onTogglePlay,
                                buttonSize = playButtonSize,
                                iconSize = playIconSize
                            )

                            MiniTransportButton(
                                icon = Icons.Default.SkipNext,
                                description = "Siguiente",
                                enabled = playback.canGoNext,
                                onClick = onNext,
                                buttonSize = controlButtonSize,
                                iconSize = controlIconSize
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun rememberMiniPulse(isActive: Boolean): Float {
    if (!isActive) return 1f
    val transition = rememberInfiniteTransition(label = "mini_pulse")
    val pulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.004f,
        animationSpec = infiniteRepeatable(
            animation = tween(PULSE_CYCLE_MS, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mini_pulse_value"
    )
    return pulse
}

@Composable
private fun TopProgressBar(
    progress: Float,
    accent: Color,
    accentGlow: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(2.dp)
            .background(PureWhite.copy(alpha = 0.06f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
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

@Composable
private fun PulsingCover(
    title: String,
    imageUrl: String?,
    coverSize: Dp,
    isActive: Boolean
) {
    val glowAlpha: Float = if (isActive) {
        val transition = rememberInfiniteTransition(label = "cover_glow")
        transition.animateFloat(
            initialValue = 0.12f,
            targetValue = 0.32f,
            animationSpec = infiniteRepeatable(
                animation = tween(PULSE_CYCLE_MS, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "cover_glow_value"
        ).value
    } else 0f

    Box(
        modifier = Modifier.size(coverSize + 8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isActive) {
            Box(
                modifier = Modifier
                    .size(coverSize + 6.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                SoftRose.copy(alpha = glowAlpha),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        Surface(
            modifier = Modifier.size(coverSize),
            shape = RoundedCornerShape(14.dp),
            color = GraphiteSurface,
            border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.06f)),
            shadowElevation = 6.dp
        ) {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    SoftRose.copy(alpha = 0.28f),
                                    MutedTeal.copy(alpha = 0.18f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title.take(1).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite.copy(alpha = 0.44f)
                    )
                }
            }
        }

        if (isActive) {
            Box(
                modifier = Modifier
                    .size(coverSize)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.Black.copy(alpha = 0.42f)),
                contentAlignment = Alignment.Center
            ) {
                MiniEqualizer()
            }
        }
    }
}

@Composable
private fun MiniEqualizer() {
    val transition = rememberInfiniteTransition(label = "mini_equalizer")
    val bar1 = transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(450),
            repeatMode = RepeatMode.Reverse
        ),
        label = "eq_b1"
    )
    val bar2 = transition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(550),
            repeatMode = RepeatMode.Reverse
        ),
        label = "eq_b2"
    )
    val bar3 = transition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "eq_b3"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.height(14.dp)
    ) {
        listOf(bar1, bar2, bar3).forEach { anim ->
            Box(
                modifier = Modifier
                    .size(width = 3.dp, height = 14.dp * anim.value)
                    .background(SoftRose, RoundedCornerShape(1.dp))
            )
        }
    }
}

@Composable
private fun MiniTransportButton(
    icon: ImageVector,
    description: String,
    enabled: Boolean,
    onClick: () -> Unit,
    buttonSize: Dp,
    iconSize: Dp
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(buttonSize)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = if (enabled) PureWhite.copy(alpha = 0.88f) else PureWhite.copy(alpha = 0.22f),
            modifier = Modifier.size(iconSize)
        )
    }
}

@Composable
private fun MiniPlayPauseButton(
    isPlaying: Boolean,
    isBuffering: Boolean,
    onClick: () -> Unit,
    buttonSize: Dp,
    iconSize: Dp
) {
    val color = if (isBuffering) MutedTeal else SoftRose
    Surface(
        modifier = Modifier.size(buttonSize),
        shape = CircleShape,
        color = color,
        shadowElevation = 6.dp
    ) {
        IconButton(onClick = onClick, enabled = !isBuffering) {
            if (isBuffering) {
                CircularProgressIndicator(
                    modifier = Modifier.size(iconSize),
                    strokeWidth = 2.dp,
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

@Composable
private fun MarqueeText(
    text: String,
    style: androidx.compose.ui.text.TextStyle,
    fontWeight: FontWeight,
    color: Color
) {
    var containerWidthPx by remember { mutableIntStateOf(0) }
    var textWidthPx by remember { mutableIntStateOf(0) }

    val density = LocalDensity.current
    val needsScroll = containerWidthPx in 1 until textWidthPx

    val translateX: Float = if (needsScroll) {
        val totalTravelPx = (textWidthPx + with(density) { 24.dp.toPx() }).toInt()
        val durationMs = (totalTravelPx / with(density) { MARQUEE_VELOCITY_DP_PER_SEC.dp.toPx() } * 1000f).toInt()
            .coerceAtLeast(4000)

        val transition = rememberInfiniteTransition(label = "marquee")
        transition.animateFloat(
            initialValue = 0f,
            targetValue = -totalTravelPx.toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(durationMs, easing = LinearEasing, delayMillis = 1500),
                repeatMode = RepeatMode.Restart
            ),
            label = "marquee_offset"
        ).value
    } else 0f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { containerWidthPx = it.size.width }
    ) {
        Row(
            modifier = Modifier.graphicsLayer { translationX = translateX }
        ) {
            Text(
                text = text,
                style = style,
                fontWeight = fontWeight,
                color = color,
                maxLines = 1,
                overflow = if (needsScroll) TextOverflow.Visible else TextOverflow.Ellipsis,
                softWrap = false,
                modifier = Modifier.onGloballyPositioned { textWidthPx = it.size.width }
            )
            if (needsScroll) {
                Spacer(modifier = Modifier.size(width = 24.dp, height = 1.dp))
                Text(
                    text = text,
                    style = style,
                    fontWeight = fontWeight,
                    color = color,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun MiniPlayerPreview() {
    EssenceAppTheme {
        MiniPlayer(
            nowPlaying = NowPlayingInfo(
                songId = 1L,
                songLookup = "songs/titi",
                title = "Titi Me Pregunto",
                artistName = "Bad Bunny",
                imageKey = null,
                durationMs = 210_000L,
                streamingUrl = "https://example.com/audio/titi.m3u8"
            ),
            playback = PlaybackUiState(
                isPlaying = true,
                positionMs = 75_000L,
                durationMs = 210_000L,
                repeatMode = PlaybackRepeatMode.Off,
                canGoPrevious = false,
                canGoNext = true
            ),
            onTogglePlay = {},
            onNext = {},
            onPrevious = {},
            onDismiss = {},
            onTap = {},
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}

@Preview(name = "Mini Player - Paused", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun MiniPlayerPausedPreview() {
    EssenceAppTheme {
        MiniPlayer(
            nowPlaying = NowPlayingInfo(
                songId = 2L,
                songLookup = "songs/moscow",
                title = "Moscow Mule Extended Version Definitive Edition",
                artistName = "Bad Bunny",
                imageKey = null,
                durationMs = 180_000L,
                streamingUrl = "https://example.com/audio/moscow.m3u8"
            ),
            playback = PlaybackUiState(
                isPlaying = false,
                positionMs = 30_000L,
                durationMs = 180_000L,
                repeatMode = PlaybackRepeatMode.One,
                canGoPrevious = true,
                canGoNext = false,
                audioOutput = AudioOutputType.BLUETOOTH_HEADSET
            ),
            onTogglePlay = {},
            onNext = {},
            onPrevious = {},
            onDismiss = {},
            onTap = {},
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}
