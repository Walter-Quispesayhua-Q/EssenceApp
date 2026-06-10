package com.essence.essenceapp.feature.song.data.repository.resolver.extraction

import android.util.Log
import com.essence.essenceapp.feature.song.data.repository.resolver.common.StreamingUrlValidator
import java.time.Clock
import java.time.Duration
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Cache en memoria de datos crudos extraidos por NewPipe.
 *
 * Evita relanzar el extractor cuando ya tenemos datos recientes y con URL
 * de streaming aun valida. Aplica doble validacion al consultar (TTL de la
 * entrada + URL no caducada) para no devolver basura.
 *
 * Tamano acotado por LRU para no crecer sin limite. Sincronizado igual que
 * el resto de caches del proyecto (AlbumDetailCache, SongDetailCache, etc.)
 * para mantener consistencia.
 */
@Singleton
class ExtractedSongDataCache @Inject constructor(
    private val clock: Clock,
    private val urlValidator: StreamingUrlValidator
) {
    private val entries = LinkedHashMap<String, CacheEntry>(MAX_ENTRIES, 0.75f, true)

    fun get(hlsMasterKey: String): ExtractedSongData? {
        val data = synchronized(entries) {
            val current = entries[hlsMasterKey]
            if (current == null) {
                null
            } else {
                val now = clock.instant()
                val isFreshEntry = current.createdAt.plus(TTL).isAfter(now)
                val hasFreshUrl = isFreshEntry && current.data.hasFreshStreamingUrl()
                if (!isFreshEntry || !hasFreshUrl) {
                    entries.remove(hlsMasterKey)
                    null
                } else {
                    current.data
                }
            }
        }
        if (data == null) {
            Log.d(TAG, "cache[$hlsMasterKey] MISS or expired")
        } else {
            Log.d(TAG, "cache[$hlsMasterKey] HIT")
        }
        return data
    }

    fun put(data: ExtractedSongData) {
        if (!data.hasFreshStreamingUrl()) {
            Log.d(TAG, "cache[${data.hlsMasterKey}] put SKIPPED (URL not fresh)")
            return
        }
        val sizeAfter = synchronized(entries) {
            // Evict LRU si llega una key nueva y estamos al limite.
            if (entries.size >= MAX_ENTRIES && !entries.containsKey(data.hlsMasterKey)) {
                val oldest = entries.keys.first()
                entries.remove(oldest)
                Log.d(TAG, "cache[$oldest] evicted by LRU (size at limit)")
            }
            entries[data.hlsMasterKey] = CacheEntry(data, clock.instant())
            entries.size
        }
        Log.d(TAG, "cache[${data.hlsMasterKey}] PUT (size=$sizeAfter)")
    }

    private fun ExtractedSongData.hasFreshStreamingUrl(): Boolean =
        urlValidator.isFresh(streamingUrl, streamingUrlExpiresAt)

    private data class CacheEntry(
        val data: ExtractedSongData,
        val createdAt: Instant
    )

    companion object {
        private const val TAG = "ExtractedSongDataCache"

        // Limite alto porque cada entrada es transitoria (TTL 5 min + URL
        // ~6h) y muy pequena (~1KB). Suficiente para sesiones largas.
        private const val MAX_ENTRIES = 256

        // Tras 1 hora forzamos una nueva extraccion aunque la URL siga
        // viva: los metadatos podrian haber cambiado en YouTube.
        private val TTL: Duration = Duration.ofHours(1)
    }
}