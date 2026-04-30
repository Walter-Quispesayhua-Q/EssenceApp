package com.essence.essenceapp.feature.song.ui.playback.manager

import android.util.Log
import com.essence.essenceapp.shared.playback.model.PlaybackQueue
import com.essence.essenceapp.shared.playback.model.PlaybackQueueItem
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "PLAYBACK_QUEUE"

@Singleton
class PlaybackQueueController @Inject constructor() {

    private val _queue = MutableStateFlow<PlaybackQueue?>(null)
    val queue: StateFlow<PlaybackQueue?> = _queue.asStateFlow()

    fun setQueue(queue: PlaybackQueue) {
        Log.d(
            TAG,
            "setQueue source=${queue.sourceKey} items=${queue.items.size} index=${queue.currentIndex}"
        )
        _queue.value = queue
    }

    fun setQueueFromItems(
        items: List<PlaybackQueueItem>,
        startIndex: Int,
        sourceKey: String
    ) {
        if (items.isEmpty()) return
        setQueue(
            PlaybackQueue(
                sourceKey = sourceKey,
                items = items,
                currentIndex = startIndex.coerceIn(0, items.lastIndex)
            )
        )
    }

    fun clear() {
        _queue.value = null
    }

    fun current(): PlaybackQueueItem? {
        val q = _queue.value ?: return null
        return q.items.getOrNull(q.currentIndex)
    }

    fun currentIndex(): Int = _queue.value?.currentIndex ?: -1

    fun moveToIndex(index: Int): PlaybackQueueItem? {
        val q = _queue.value ?: return null
        val item = q.items.getOrNull(index) ?: return null
        _queue.value = q.withIndex(index)
        return item
    }

    fun advanceToNext(): PlaybackQueueItem? {
        val q = _queue.value ?: return null
        val nextIndex = q.currentIndex + 1
        if (nextIndex > q.items.lastIndex) return null
        return moveToIndex(nextIndex)
    }

    fun retreatToPrevious(): PlaybackQueueItem? {
        val q = _queue.value ?: return null
        val prevIndex = q.currentIndex - 1
        if (prevIndex < 0) return null
        return moveToIndex(prevIndex)
    }

    fun peekNext(): PlaybackQueueItem? {
        val q = _queue.value ?: return null
        return q.items.getOrNull(q.currentIndex + 1)
    }

    fun alignIndex(songLookup: String) {
        val q = _queue.value ?: return
        val foundIndex = q.items.indexOfFirst { it.songLookup == songLookup }
        if (foundIndex >= 0 && foundIndex != q.currentIndex) {
            _queue.value = q.withIndex(foundIndex)
            Log.d(TAG, "alignIndex: $songLookup -> index $foundIndex")
        }
    }

    val canGoPrevious: Boolean
        get() = _queue.value?.canGoPrevious == true

    val canGoNext: Boolean
        get() = _queue.value?.canGoNext == true
}
