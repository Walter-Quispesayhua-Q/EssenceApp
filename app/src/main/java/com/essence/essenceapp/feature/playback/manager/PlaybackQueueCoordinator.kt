package com.essence.essenceapp.feature.playback.manager

import com.essence.essenceapp.feature.playback.domain.PlaybackQueue
import com.essence.essenceapp.feature.playback.domain.PlaybackQueueItem
import com.essence.essenceapp.feature.playback.domain.PlaybackShuffleMode
import com.essence.essenceapp.feature.playback.prefetch.PlaybackPrefetchCoordinator
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Coordina cambios de cola y prefetch.
 *
 * Usa reglas puras de PlaybackQueueController, guarda el resultado en el
 * estado compartido y avisa al prefetch cuando cambia la proxima cancion.
 */
@Singleton
class PlaybackQueueCoordinator @Inject constructor(
    private val stateStore: PlaybackStateStore,
    private val prefetchCoordinator: PlaybackPrefetchCoordinator
) {
    private val queueController = PlaybackQueueController()

    fun open(
        items: List<PlaybackQueueItem>,
        startIndex: Int
    ): Boolean {
        val queue = queueController.open(items, startIndex) ?: return false
        applyQueue(queue)
        return true
    }

    fun next(shuffleMode: PlaybackShuffleMode): Boolean {
        val queue = stateStore.currentQueue ?: return false

        val nextQueue = if (shuffleMode == PlaybackShuffleMode.ON) {
            randomNextQueue(queue)
        } else {
            queueController.next(queue)
        } ?: return false

        applyQueue(nextQueue)
        return true
    }

    fun previous(): Boolean {
        val queue = stateStore.currentQueue ?: return false
        val previousQueue = queueController.previous(queue) ?: return false

        applyQueue(previousQueue)
        return true
    }

    fun skipTo(index: Int): Boolean {
        val queue = stateStore.currentQueue ?: return false
        val targetQueue = queueController.skipTo(queue, index) ?: return false

        applyQueue(targetQueue)
        return true
    }

    fun rewindToFirst(): Boolean {
        val queue = stateStore.currentQueue ?: return false
        if (queue.items.isEmpty()) return false

        applyQueue(queue.withIndex(0))
        return true
    }

    fun addNext(item: PlaybackQueueItem) {
        val queue = stateStore.currentQueue

        val updatedQueue = if (queue == null) {
            PlaybackQueue(items = listOf(item), currentIndex = 0)
        } else {
            queueController.addNext(queue, item)
        }

        applyQueue(updatedQueue)
    }

    fun addEnd(item: PlaybackQueueItem) {
        val queue = stateStore.currentQueue

        val updatedQueue = if (queue == null) {
            PlaybackQueue(items = listOf(item), currentIndex = 0)
        } else {
            queueController.addEnd(queue, item)
        }

        applyQueue(updatedQueue)
    }

    fun removeAt(index: Int): QueueMutationResult {
        val queue = stateStore.currentQueue ?: return QueueMutationResult.Unchanged
        if (index !in queue.items.indices) return QueueMutationResult.Unchanged

        val removingCurrent = index == queue.currentIndex
        val updatedQueue = queueController.removeAt(queue, index)

        if (updatedQueue == null) {
            stop()
            return QueueMutationResult.Emptied
        }

        applyQueue(updatedQueue)

        return if (removingCurrent) {
            QueueMutationResult.CurrentChanged
        } else {
            QueueMutationResult.Changed
        }
    }

    fun move(
        from: Int,
        to: Int
    ): QueueMutationResult {
        val queue = stateStore.currentQueue ?: return QueueMutationResult.Unchanged
        if (from !in queue.items.indices) return QueueMutationResult.Unchanged
        if (to !in queue.items.indices) return QueueMutationResult.Unchanged
        if (from == to) return QueueMutationResult.Unchanged

        applyQueue(queueController.move(queue, from, to))
        return QueueMutationResult.Changed
    }

    fun clearUpcoming(): QueueMutationResult {
        val queue = stateStore.currentQueue ?: return QueueMutationResult.Unchanged
        if (queue.upcoming.isEmpty()) return QueueMutationResult.Unchanged

        applyQueue(queueController.clearUpcoming(queue))
        return QueueMutationResult.Changed
    }

    fun prefetchUpcoming() {
        prefetchCoordinator.prefetchNext(stateStore.currentQueue)
    }

    fun cancelPrefetch() {
        prefetchCoordinator.cancel()
    }

    fun stop() {
        prefetchCoordinator.cancel()
        stateStore.clearPlayback()
    }

    private fun applyQueue(queue: PlaybackQueue) {
        stateStore.setQueue(queue)
    }

    private fun randomNextQueue(queue: PlaybackQueue): PlaybackQueue? {
        if (queue.items.size <= 1) return null

        val candidates = queue.items.indices.filter { it != queue.currentIndex }
        if (candidates.isEmpty()) return null

        return queue.withIndex(candidates[Random.nextInt(candidates.size)])
    }

    enum class QueueMutationResult {
        Unchanged,
        Changed,
        CurrentChanged,
        Emptied
    }
}
