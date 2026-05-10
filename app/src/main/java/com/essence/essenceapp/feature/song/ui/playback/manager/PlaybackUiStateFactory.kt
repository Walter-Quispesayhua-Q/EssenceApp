package com.essence.essenceapp.feature.song.ui.playback.manager

import com.essence.essenceapp.feature.song.ui.playback.AudioPlayerState
import com.essence.essenceapp.feature.song.ui.playback.PlaybackUiState
import com.essence.essenceapp.feature.song.ui.playback.engine.AudioOutputType
import com.essence.essenceapp.shared.playback.model.PlaybackQueue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

fun createPlaybackUiStateFlow(
    scope: CoroutineScope,
    audioPlayerState: StateFlow<AudioPlayerState>,
    queue: StateFlow<PlaybackQueue?>,
    audioOutputType: StateFlow<AudioOutputType>,
    isResolvingNextSong: StateFlow<Boolean>,
    manualErrorMessage: StateFlow<String?>
): StateFlow<PlaybackUiState> = combine(
    audioPlayerState,
    queue,
    audioOutputType,
    isResolvingNextSong,
    manualErrorMessage
) { audioState, currentQueue, output, resolving, manualError ->
    PlaybackUiState(
        isPlaying = !resolving && audioState.isPlaying,
        isBuffering = audioState.isBuffering || resolving,
        positionMs = audioState.positionMs,
        durationMs = audioState.durationMs,
        repeatMode = audioState.repeatMode,
        canGoPrevious = currentQueue?.canGoPrevious == true || audioState.durationMs > 0L,
        canGoNext = currentQueue?.canGoNext == true,
        errorMessage = manualError ?: audioState.errorMessage,
        audioOutput = output
    )
}.stateIn(scope, SharingStarted.Eagerly, PlaybackUiState())
