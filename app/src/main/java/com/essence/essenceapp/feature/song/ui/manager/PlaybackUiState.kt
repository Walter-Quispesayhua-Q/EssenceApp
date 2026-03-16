package com.essence.essenceapp.feature.song.ui.manager

data class PlaybackUiState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val isRepeat: Boolean = false
)