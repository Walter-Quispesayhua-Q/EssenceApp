package com.essence.essenceapp.feature.song.ui.playback.manager

import com.essence.essenceapp.feature.song.domain.model.Song
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResolvedSongCache @Inject constructor() {

    private val cache = ConcurrentHashMap<String, Song>()

    fun get(lookup: String): Song? = cache[lookup]

    fun put(lookup: String, song: Song) {
        cache[lookup] = song
    }

    fun remove(lookup: String) {
        cache.remove(lookup)
    }

    fun clear() {
        cache.clear()
    }
}
