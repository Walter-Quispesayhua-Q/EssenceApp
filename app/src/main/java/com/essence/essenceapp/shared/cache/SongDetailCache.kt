package com.essence.essenceapp.shared.cache

import com.essence.essenceapp.feature.song.domain.model.Song
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Cache en memoria para las últimas canciones vistas en SongDetail.
 * Evita peticiones innecesarias al volver a entrar a una canción reciente.
 * Máximo 15 canciones (LRU). Cada Song pesa ~1-2 KB de metadatos, asi que
 * el costo en memoria es despreciable y mejora notablemente el cache hit
 * rate cuando el usuario navega entre varias canciones.
 */
@Singleton
class SongDetailCache @Inject constructor() {

    private val cache = LinkedHashMap<String, Song>(MAX_SIZE, 0.75f, true)

    fun get(lookup: String): Song? = synchronized(cache) { cache[lookup] }

    fun put(lookup: String, song: Song) = synchronized(cache) {
        if (cache.size >= MAX_SIZE && !cache.containsKey(lookup)) {
            val oldest = cache.keys.first()
            cache.remove(oldest)
        }
        cache[lookup] = song
    }

    companion object {
        private const val MAX_SIZE = 15
    }
}
