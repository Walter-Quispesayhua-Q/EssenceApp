package com.essence.essenceapp.shared.cache

import android.util.Log
import com.essence.essenceapp.feature.song.domain.model.SongSimple
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QueueCache @Inject constructor() {

    private val sources = LinkedHashMap<String, List<SongSimple>>(MAX_SOURCES, 0.75f, true)

    fun set(source: String, newItems: List<SongSimple>) = synchronized(sources) {
        Log.d(TAG, "Set: $source (${newItems.size} items)")
        if (sources.size >= MAX_SOURCES && !sources.containsKey(source)) {
            val oldest = sources.keys.first()
            sources.remove(oldest)
            Log.d(TAG, "Evict (LRU slots): $oldest")
        }
        sources[source] = newItems
        enforceTotalSongLimit(protectedSource = source)
    }

    private fun enforceTotalSongLimit(protectedSource: String) {
        var total = sources.values.sumOf { it.size }
        while (total > MAX_TOTAL_SONGS && sources.size > 1) {
            val oldestKey = sources.keys.firstOrNull { it != protectedSource } ?: break
            val removed = sources.remove(oldestKey) ?: break
            total -= removed.size
            Log.d(TAG, "Evict (LRU total): $oldestKey (-${removed.size} items, total=$total)")
        }
    }

    fun findItem(songLookup: String): SongSimple? = synchronized(sources) {
        val ordered = sources.entries.toList().asReversed()
        for ((_, items) in ordered) {
            val match = items.find { it.hlsMasterKey == songLookup }
            if (match != null) return match
        }
        null
    }

    fun getSource(source: String): List<SongSimple>? = synchronized(sources) {
        sources[source]
    }

    fun invalidate(source: String) = synchronized(sources) {
        sources.remove(source)
    }

    fun clear() = synchronized(sources) {
        sources.clear()
    }

    companion object {
        private const val TAG = "QueueCache"
        private const val MAX_SOURCES = 10
        private const val MAX_TOTAL_SONGS = 20_000
    }
}