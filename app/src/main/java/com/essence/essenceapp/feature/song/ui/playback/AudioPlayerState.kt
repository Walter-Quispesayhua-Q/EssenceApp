package com.essence.essenceapp.feature.song.ui.playback

data class AudioPlayerState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val currentUrl: String? = null,
    val repeatMode: PlaybackRepeatMode = PlaybackRepeatMode.Off,
    val errorMessage: String? = null,
    val hasEnded: Boolean = false,
    val requiresSourceRefresh: Boolean = false
)