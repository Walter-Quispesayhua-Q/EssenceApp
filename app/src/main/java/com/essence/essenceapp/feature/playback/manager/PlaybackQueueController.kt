package com.essence.essenceapp.feature.playback.manager

import com.essence.essenceapp.feature.playback.domain.PlaybackQueue
import com.essence.essenceapp.feature.playback.domain.PlaybackQueueItem

/**
 * Aplica reglas puras de cola.
 *
 * No reproduce audio, no resuelve canciones y no conoce Media3. Solo recibe
 * una cola y devuelve una cola nueva cuando la operacion es valida.
 */
class PlaybackQueueController {

    fun open(
        items: List<PlaybackQueueItem>,
        startIndex: Int
    ): PlaybackQueue? {
        if (items.isEmpty()) return null

        return PlaybackQueue(
            items = items,
            currentIndex = startIndex.coerceIn(items.indices)
        )
    }

    fun next(queue: PlaybackQueue): PlaybackQueue? {
        if (!queue.hasNext) return null
        return queue.withIndex(queue.currentIndex + 1)
    }

    fun previous(queue: PlaybackQueue): PlaybackQueue? {
        if (!queue.hasPrevious) return null
        return queue.withIndex(queue.currentIndex - 1)
    }

    fun skipTo(
        queue: PlaybackQueue,
        index: Int
    ): PlaybackQueue? {
        if (index !in queue.items.indices) return null
        return queue.withIndex(index)
    }

    fun addNext(
        queue: PlaybackQueue,
        item: PlaybackQueueItem
    ): PlaybackQueue {
        val insertIndex = (queue.currentIndex + 1).coerceAtMost(queue.items.size)

        return queue.copy(
            items = queue.items.toMutableList().apply {
                add(insertIndex, item)
            }
        )
    }

    fun addEnd(
        queue: PlaybackQueue,
        item: PlaybackQueueItem
    ): PlaybackQueue =
        queue.copy(items = queue.items + item)

    fun removeAt(
        queue: PlaybackQueue,
        index: Int
    ): PlaybackQueue? {
        if (index !in queue.items.indices) return queue
        if (queue.items.size == 1) return null

        val newItems = queue.items.toMutableList().apply {
            removeAt(index)
        }

        val newIndex = when {
            index < queue.currentIndex -> queue.currentIndex - 1
            index == queue.currentIndex -> queue.currentIndex.coerceAtMost(newItems.lastIndex)
            else -> queue.currentIndex
        }

        return PlaybackQueue(
            items = newItems,
            currentIndex = newIndex
        )
    }

    fun move(
        queue: PlaybackQueue,
        from: Int,
        to: Int
    ): PlaybackQueue {
        if (from !in queue.items.indices) return queue
        if (to !in queue.items.indices) return queue
        if (from == to) return queue

        val items = queue.items.toMutableList()
        val moved = items.removeAt(from)
        items.add(to, moved)

        val newIndex = when (queue.currentIndex) {
            from -> to
            in (from + 1)..to -> queue.currentIndex - 1
            in to until from -> queue.currentIndex + 1
            else -> queue.currentIndex
        }

        return PlaybackQueue(
            items = items,
            currentIndex = newIndex
        )
    }

    fun clearUpcoming(queue: PlaybackQueue): PlaybackQueue =
        queue.copy(
            items = queue.items.take(queue.currentIndex + 1)
        )
}