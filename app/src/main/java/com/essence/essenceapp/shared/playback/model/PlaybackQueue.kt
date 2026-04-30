package com.essence.essenceapp.shared.playback.model

data class PlaybackQueue(
    val sourceKey: String,
    val items: List<PlaybackQueueItem>,
    val currentIndex: Int
) {
    val currentItem: PlaybackQueueItem?
        get() = items.getOrNull(currentIndex)

    val canGoNext: Boolean
        get() = currentIndex < items.lastIndex

    val canGoPrevious: Boolean
        get() = currentIndex > 0

    fun withIndex(index: Int): PlaybackQueue =
        copy(currentIndex = index.coerceIn(0, items.lastIndex))
}
