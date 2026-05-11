package com.essence.essenceapp.feature.song.ui.playback.manager

import com.essence.essenceapp.feature.song.domain.model.Song
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResolvedSongCache @Inject constructor() {

    private val byLookup = ConcurrentHashMap<String, Song>()
    private val songIdToLookup = ConcurrentHashMap<Long, String>()

    fun get(lookup: String): Song? = byLookup[lookup]

    fun getBySongId(songId: Long): Song? {
        val lookup = songIdToLookup[songId] ?: return null
        return byLookup[lookup]
    }

    fun put(lookup: String, song: Song) {
        val previous = byLookup.put(lookup, song)
        if (previous != null && previous.id != song.id) {
            songIdToLookup.remove(previous.id, lookup)
        }
        if (song.id > 0L) {
            songIdToLookup[song.id] = lookup
        }
    }

    fun remove(lookup: String) {
        val previous = byLookup.remove(lookup)
        if (previous != null && previous.id > 0L) {
            songIdToLookup.remove(previous.id, lookup)
        }
    }

    fun removeBySongId(songId: Long) {
        val lookup = songIdToLookup.remove(songId) ?: return
        byLookup.remove(lookup)
    }

    fun invalidateExpired(isExpired: (Song) -> Boolean): Int {
        var removed = 0
        val iterator = byLookup.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            val song = entry.value
            if (isExpired(song)) {
                iterator.remove()
                if (song.id > 0L) {
                    songIdToLookup.remove(song.id, entry.key)
                }
                removed++
            }
        }
        return removed
    }

    fun clear() {
        byLookup.clear()
        songIdToLookup.clear()
    }
}
