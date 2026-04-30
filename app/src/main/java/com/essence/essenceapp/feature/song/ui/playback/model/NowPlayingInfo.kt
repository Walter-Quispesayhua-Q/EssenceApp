package com.essence.essenceapp.feature.song.ui.playback.model

data class NowPlayingInfo(
    val songId: Long,
    val songLookup: String,
    val title: String,
    val artistName: String,
    val imageKey: String?,
    val durationMs: Long,
    val streamingUrl: String
)