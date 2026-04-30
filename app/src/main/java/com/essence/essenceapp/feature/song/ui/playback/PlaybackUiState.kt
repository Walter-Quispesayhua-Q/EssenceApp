package com.essence.essenceapp.feature.song.ui.playback

import com.essence.essenceapp.feature.song.ui.playback.engine.AudioOutputType

data class PlaybackUiState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val repeatMode: PlaybackRepeatMode = PlaybackRepeatMode.Off,
    val canGoPrevious: Boolean = false,
    val canGoNext: Boolean = false,
    val errorMessage: String? = null,
    val audioOutput: AudioOutputType = AudioOutputType.PHONE_SPEAKER
)