package com.essence.essenceapp.feature.playback.manager.resolver

import com.essence.essenceapp.feature.song.data.repository.resolver.common.StreamingUrlValidator
import com.essence.essenceapp.feature.song.domain.model.Song
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Cache de Song ya resuelta dentro de playback.
 *
 * Acelera replay, skip-back y recovery porque evita volver al backend o al
 * extractor cuando ya tenemos una Song con URL todavia valida en memoria.
 *
 * No persiste a disco: se vacia al matar el proceso. La URL deja de servir
 * cuando el validador la considera caducada (o cuando le queda menos buffer
 * del minimo), y en ese momento se descarta del cache de forma perezosa.
 *
 * El cache no rastrea cambios de like u otros metadatos: solo es un atajo
 * para tener una Song reproducible. La UI sigue siendo la fuente de verdad
 * para los flags interactivos (like, etc).
 */
@Singleton
class PlaybackResolvedSongCache @Inject constructor(
    private val urlValidator: StreamingUrlValidator
) {
    private val byKey = ConcurrentHashMap<String, Song>()
    private val songIdToKey = ConcurrentHashMap<Long, String>()

    fun get(hlsMasterKey: String): Song? {
        val key = hlsMasterKey.takeIf { it.isNotBlank() } ?: return null
        val cached = byKey[key] ?: return null
        if (!urlValidator.isFresh(cached.streamingUrl, cached.streamingUrlExpiresAt)) {
            invalidate(key)
            return null
        }
        return cached
    }

    fun getBySongId(songId: Long): Song? {
        val key = songIdToKey[songId] ?: return null
        return get(key)
    }

    fun put(song: Song) {
        val key = song.hlsMasterKey.takeIf { it.isNotBlank() } ?: return
        if (song.streamingUrl.isNullOrBlank()) return

        val previous = byKey.put(key, song)
        if (previous != null && previous.id > 0L && previous.id != song.id) {
            songIdToKey.remove(previous.id, key)
        }
        if (song.id > 0L) {
            songIdToKey[song.id] = key
        }
    }

    fun invalidate(hlsMasterKey: String) {
        val removed = byKey.remove(hlsMasterKey) ?: return
        if (removed.id > 0L) {
            songIdToKey.remove(removed.id, hlsMasterKey)
        }
    }

    fun invalidateBySongId(songId: Long) {
        val key = songIdToKey.remove(songId) ?: return
        byKey.remove(key)
    }

    fun clear() {
        byKey.clear()
        songIdToKey.clear()
    }
}
