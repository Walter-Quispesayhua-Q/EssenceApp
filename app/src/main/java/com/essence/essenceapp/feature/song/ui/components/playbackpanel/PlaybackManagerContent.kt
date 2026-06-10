package com.essence.essenceapp.feature.song.ui.components.playbackpanel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.playback.domain.NowPlayingInfo
import com.essence.essenceapp.feature.playback.domain.PlaybackAction
import com.essence.essenceapp.feature.playback.domain.PlaybackError
import com.essence.essenceapp.feature.playback.domain.PlaybackPositionInfo
import com.essence.essenceapp.feature.playback.domain.PlaybackQueueItem
import com.essence.essenceapp.feature.playback.domain.PlaybackRepeatMode
import com.essence.essenceapp.feature.playback.domain.PlaybackShuffleMode
import com.essence.essenceapp.feature.playback.domain.PlaybackState
import com.essence.essenceapp.feature.playback.domain.PlaybackUiState
import com.essence.essenceapp.ui.theme.EssenceAppTheme

/**
 * Panel grande de controles usado dentro de SongDetail.
 *
 * Este componente pertenece a song porque usa el estilo visual de esa pantalla.
 * Recibe estado de playback y devuelve acciones hacia el controlador.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaybackManagerContent(
    state: PlaybackUiState,
    onAction: (PlaybackAction) -> Unit,
    modifier: Modifier = Modifier,
    songTitle: String? = state.nowPlaying?.item?.title,
    artistName: String? = state.nowPlaying?.item?.artistName,
    isLiked: Boolean = state.nowPlaying?.isLiked == true,
    isLikeSubmitting: Boolean = false,
    onToggleLike: (() -> Unit)? = null,
    showMetaHeader: Boolean = true
) {
    val isPlaying = state.isPlaying
    val isBuffering = state.isBuffering
    val colors = PlaybackPanelDefaults.colors(
        isPlaying = isPlaying,
        isBuffering = isBuffering
    )

    val failedState = state.playbackState as? PlaybackState.Failed
    val errorMessage = failedState?.cause?.message

    val haptic = LocalHapticFeedback.current

    PlaybackPanelSurface(
        modifier = modifier,
        isPlaying = isPlaying,
        isBuffering = isBuffering,
        colors = colors
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val sizes = PlaybackPanelDefaults.sizes(
                compactMode = maxWidth < 335.dp
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (showMetaHeader) {
                    PlaybackMetaHeader(
                        songTitle = songTitle,
                        artistName = artistName,
                        isBuffering = isBuffering,
                        isPlaying = isPlaying,
                        accent = colors.accent,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                PlaybackSeekBar(
                    positionMs = state.position.positionMs,
                    durationMs = state.position.durationMs,
                    isBuffering = isBuffering,
                    sliderColor = colors.sliderColor,
                    sliderGlow = colors.sliderGlow,
                    onSeekTo = { positionMs ->
                        onAction(PlaybackAction.SeekTo(positionMs))
                    }
                )

                PlaybackTransportControls(
                    repeatMode = state.repeatMode,
                    isPlaying = isPlaying,
                    isBuffering = isBuffering,
                    canGoPrevious = state.nowPlaying?.canSkipPrevious == true,
                    canGoNext = state.nowPlaying?.canSkipNext == true,
                    isLiked = isLiked,
                    isLikeSubmitting = isLikeSubmitting,
                    onToggleRepeat = {
                        onAction(PlaybackAction.ToggleRepeat)
                    },
                    onPrevious = {
                        onAction(PlaybackAction.Previous)
                    },
                    onPlayPause = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onAction(
                            if (isPlaying || isBuffering) {
                                PlaybackAction.Pause
                            } else {
                                PlaybackAction.Play
                            }
                        )
                    },
                    onNext = {
                        onAction(PlaybackAction.Next)
                    },
                    onToggleLike = onToggleLike,
                    sizes = sizes,
                    modifier = Modifier.fillMaxWidth()
                )

                errorMessage?.takeIf { it.isNotBlank() }?.let { message ->
                    PlaybackErrorRow(
                        message = message,
                        onRetry = { onAction(PlaybackAction.Play) }
                    )
                }
            }
        }
    }
}

@Preview(name = "Playback Panel - Playing", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PlaybackManagerContentPlayingPreview() {
    EssenceAppTheme {
        PlaybackManagerContent(
            state = previewPanelState(
                playbackState = PlaybackState.Playing,
                canSkipPrevious = true,
                canSkipNext = true
            ),
            onAction = {},
            isLiked = true,
            onToggleLike = {}
        )
    }
}

@Preview(name = "Playback Panel - Buffering", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PlaybackManagerContentBufferingPreview() {
    EssenceAppTheme {
        PlaybackManagerContent(
            state = previewPanelState(
                playbackState = PlaybackState.Buffering,
                canSkipPrevious = false,
                canSkipNext = true
            ),
            onAction = {},
            isLiked = false,
            onToggleLike = {}
        )
    }
}

@Preview(name = "Playback Panel - Failed", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PlaybackManagerContentFailedPreview() {
    EssenceAppTheme {
        PlaybackManagerContent(
            state = previewPanelState(
                playbackState = PlaybackState.Failed(
                    PlaybackError.Network("No se pudo cargar el audio.")
                ),
                canSkipPrevious = true,
                canSkipNext = true
            ),
            onAction = {},
            isLiked = false,
            onToggleLike = {}
        )
    }
}

private fun previewPanelState(
    playbackState: PlaybackState,
    canSkipPrevious: Boolean,
    canSkipNext: Boolean
): PlaybackUiState =
    PlaybackUiState(
        nowPlaying = NowPlayingInfo(
            item = PlaybackQueueItem(
                hlsMasterKey = "preview-key",
                songId = 1L,
                title = "Everybody Wants To Rule The World",
                artistName = "Tears For Fears",
                imageKey = null,
                durationMs = 240_000L
            ),
            isLiked = false,
            canSkipNext = canSkipNext,
            canSkipPrevious = canSkipPrevious
        ),
        queue = null,
        position = PlaybackPositionInfo(
            positionMs = 125_000L,
            durationMs = 240_000L,
            bufferedMs = 160_000L
        ),
        playbackState = playbackState,
        repeatMode = PlaybackRepeatMode.ONE,
        shuffleMode = PlaybackShuffleMode.OFF
    )