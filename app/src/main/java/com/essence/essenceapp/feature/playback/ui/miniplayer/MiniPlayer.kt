package com.essence.essenceapp.feature.playback.ui.miniplayer

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.core.network.resolveImageUrl
import com.essence.essenceapp.feature.playback.domain.NowPlayingInfo
import com.essence.essenceapp.feature.playback.domain.PlaybackPositionInfo
import com.essence.essenceapp.feature.playback.domain.PlaybackQueueItem
import com.essence.essenceapp.feature.playback.domain.PlaybackRepeatMode
import com.essence.essenceapp.feature.playback.domain.PlaybackShuffleMode
import com.essence.essenceapp.feature.playback.domain.PlaybackState
import com.essence.essenceapp.feature.playback.domain.PlaybackUiState
import com.essence.essenceapp.feature.playback.engine.AudioOutputType
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Mini reproductor compacto para mostrar la cancion actual.
 *
 * Solo dibuja estado y envia acciones hacia afuera. No resuelve canciones, no
 * controla Media3 directamente y no modifica la cola por su cuenta.
 */
@Composable
fun MiniPlayer(
    nowPlaying: NowPlayingInfo,
    playback: PlaybackUiState,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onDismiss: () -> Unit,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
    audioOutputType: AudioOutputType = AudioOutputType.UNKNOWN
) {
    val item = nowPlaying.item
    val progress = playback.position.progress
    val imageUrl = resolveImageUrl(item.imageKey)
    val isPlaying = playback.isPlaying
    val isBuffering = playback.isBuffering
    val isActive = isPlaying && !isBuffering
    val colors = MiniPlayerDefaults.colors(
        isPlaying = isPlaying,
        isBuffering = isBuffering
    )

    val pulseScale = rememberMiniPulse(isActive = isActive)

    val isLongPressing = remember { mutableStateOf(false) }
    val longPressScale by animateFloatAsState(
        targetValue = if (isLongPressing.value) 0.94f else 1f,
        animationSpec = tween(
            durationMillis = MiniPlayerDefaults.LongPressAnimationMs,
            easing = FastOutSlowInEasing
        ),
        label = "mini_long_press_scale"
    )
    val longPressAlpha by animateFloatAsState(
        targetValue = if (isLongPressing.value) 0.78f else 1f,
        animationSpec = tween(
            durationMillis = MiniPlayerDefaults.LongPressAnimationMs,
            easing = FastOutSlowInEasing
        ),
        label = "mini_long_press_alpha"
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
            .clip(MiniPlayerDefaults.ContainerShape)
            .pointerInput(onDismiss, onTap) {
                detectTapGestures(
                    onPress = {
                        coroutineScope {
                            val hintJob = launch {
                                delay(MiniPlayerDefaults.LongPressHintDelayMs)
                                isLongPressing.value = true
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }

                            try {
                                tryAwaitRelease()
                            } finally {
                                hintJob.cancel()
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
        shape = MiniPlayerDefaults.ContainerShape,
        color = GraphiteSurface.copy(alpha = 0.90f),
        border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.07f)),
        shadowElevation = 12.dp
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            MiniPlayerBackground(accent = colors.accent)

            Column(modifier = Modifier.fillMaxWidth()) {
                MiniPlayerProgressBar(
                    progress = progress,
                    accent = colors.accent,
                    accentGlow = colors.accentGlow
                )

                MiniPlayerBody(
                    title = item.title,
                    artistName = item.artistName,
                    imageUrl = imageUrl,
                    isActive = isActive,
                    isPlaying = isPlaying,
                    isBuffering = isBuffering,
                    canSkipPrevious = nowPlaying.canSkipPrevious,
                    canSkipNext = nowPlaying.canSkipNext,
                    audioOutputType = audioOutputType,
                    onPrevious = onPrevious,
                    onNext = onNext,
                    onTogglePlay = onTogglePlay
                )
            }
        }
    }
}

@Composable
private fun MiniPlayerBackground(accent: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
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
}

@Composable
private fun MiniPlayerBody(
    title: String,
    artistName: String,
    imageUrl: String?,
    isActive: Boolean,
    isPlaying: Boolean,
    isBuffering: Boolean,
    canSkipPrevious: Boolean,
    canSkipNext: Boolean,
    audioOutputType: AudioOutputType,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onTogglePlay: () -> Unit
) {
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
            MiniPlayerCover(
                title = title,
                imageUrl = imageUrl,
                coverSize = coverSize,
                isActive = isActive
            )

            MiniPlayerTextBlock(
                title = title,
                artistName = artistName,
                audioOutputType = audioOutputType,
                modifier = Modifier.weight(1f)
            )

            Row(
                modifier = Modifier.widthIn(max = controlsWidth),
                horizontalArrangement = Arrangement.spacedBy(spacing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MiniPreviousButton(
                    enabled = canSkipPrevious,
                    onClick = onPrevious,
                    buttonSize = controlButtonSize,
                    iconSize = controlIconSize
                )

                MiniPlayPauseButton(
                    isPlaying = isPlaying,
                    isBuffering = isBuffering,
                    onClick = onTogglePlay,
                    buttonSize = playButtonSize,
                    iconSize = playIconSize
                )

                MiniNextButton(
                    enabled = canSkipNext,
                    onClick = onNext,
                    buttonSize = controlButtonSize,
                    iconSize = controlIconSize
                )
            }
        }
    }
}

@Composable
private fun MiniPlayerTextBlock(
    title: String,
    artistName: String,
    audioOutputType: AudioOutputType,
    modifier: Modifier = Modifier
) {
    val audioOutput = audioOutputType.toMiniPlayerAudioOutputInfo()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        MiniPlayerMarqueeText(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = PureWhite
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = audioOutput.icon,
                contentDescription = audioOutput.label,
                tint = MutedTeal.copy(alpha = 0.72f),
                modifier = Modifier.size(11.dp)
            )

            Text(
                text = artistName,
                style = MaterialTheme.typography.labelSmall,
                color = PureWhite.copy(alpha = 0.56f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun rememberMiniPulse(isActive: Boolean): Float {
    val transition = rememberInfiniteTransition(label = "mini_pulse")
    val pulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.004f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = MiniPlayerDefaults.PulseCycleMs,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mini_pulse_value"
    )

    return if (isActive) pulse else 1f
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun MiniPlayerPreview() {
    EssenceAppTheme {
        MiniPlayer(
            nowPlaying = previewNowPlaying(canSkipNext = true),
            playback = previewPlaybackState(PlaybackState.Playing),
            audioOutputType = AudioOutputType.BLUETOOTH_HEADSET,
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
            nowPlaying = previewNowPlaying(
                title = "Moscow Mule Extended Version Definitive Edition",
                canSkipPrevious = true
            ),
            playback = previewPlaybackState(PlaybackState.Paused),
            audioOutputType = AudioOutputType.PHONE_SPEAKER,
            onTogglePlay = {},
            onNext = {},
            onPrevious = {},
            onDismiss = {},
            onTap = {},
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}

private fun previewNowPlaying(
    title: String = "Titi Me Pregunto",
    canSkipNext: Boolean = false,
    canSkipPrevious: Boolean = false
): NowPlayingInfo =
    NowPlayingInfo(
        item = PlaybackQueueItem(
            hlsMasterKey = "songs/preview",
            songId = 1L,
            title = title,
            artistName = "Bad Bunny",
            imageKey = null,
            durationMs = 210_000L
        ),
        isLiked = false,
        canSkipNext = canSkipNext,
        canSkipPrevious = canSkipPrevious
    )

private fun previewPlaybackState(state: PlaybackState): PlaybackUiState =
    PlaybackUiState(
        nowPlaying = null,
        queue = null,
        position = PlaybackPositionInfo(
            positionMs = 75_000L,
            durationMs = 210_000L,
            bufferedMs = 120_000L
        ),
        playbackState = state,
        repeatMode = PlaybackRepeatMode.OFF,
        shuffleMode = PlaybackShuffleMode.OFF
    )