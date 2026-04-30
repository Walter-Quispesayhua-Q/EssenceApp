package com.essence.essenceapp.shared.playback.model

data class PlaybackQueueItem(
    val songLookup: String,
    val title: String,
    val artistName: String,
    val imageKey: String?,
    val durationMs: Long
)
