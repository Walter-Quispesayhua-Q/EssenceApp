package com.essence.essenceapp.shared.cache

import com.essence.essenceapp.feature.album.domain.model.Album
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlbumDetailCache @Inject constructor() {

    private val cache = LinkedHashMap<String, Album>(MAX_SIZE, 0.75f, true)

    fun get(lookup: String): Album? = synchronized(cache) { cache[lookup] }

    fun put(lookup: String, album: Album) = synchronized(cache) {
        if (cache.size >= MAX_SIZE && !cache.containsKey(lookup)) {
            val oldest = cache.keys.first()
            cache.remove(oldest)
        }
        cache[lookup] = album
    }

    fun invalidate(lookup: String) = synchronized(cache) { cache.remove(lookup) }

    companion object {
        private const val MAX_SIZE = 15
    }
}
