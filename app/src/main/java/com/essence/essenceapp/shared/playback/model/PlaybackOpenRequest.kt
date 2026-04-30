package com.essence.essenceapp.shared.playback.model

data class PlaybackOpenRequest(
    val songLookup: String,
    val queue: List<PlaybackQueueItem>,
    val startIndex: Int,
    val sourceKey: String
)
