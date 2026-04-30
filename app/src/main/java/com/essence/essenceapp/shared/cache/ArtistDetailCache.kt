package com.essence.essenceapp.shared.cache

import com.essence.essenceapp.feature.artist.domain.model.Artist
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArtistDetailCache @Inject constructor() {

    private val cache = LinkedHashMap<String, Artist>(MAX_SIZE, 0.75f, true)

    fun get(lookup: String): Artist? = synchronized(cache) { cache[lookup] }

    fun put(lookup: String, artist: Artist) = synchronized(cache) {
        if (cache.size >= MAX_SIZE && !cache.containsKey(lookup)) {
            val oldest = cache.keys.first()
            cache.remove(oldest)
        }
        cache[lookup] = artist
    }

    fun invalidate(lookup: String) = synchronized(cache) { cache.remove(lookup) }

    companion object {
        private const val MAX_SIZE = 15
    }
}
