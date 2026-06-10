package com.essence.essenceapp.feature.playback.manager.resolver

import com.essence.essenceapp.feature.playback.domain.PlaybackQueueItem
import com.essence.essenceapp.feature.song.domain.model.Song
import com.essence.essenceapp.feature.song.domain.usecase.GetSongUseCase
import com.essence.essenceapp.feature.song.domain.usecase.RefreshStreamingUrlUseCase
import com.essence.essenceapp.feature.song.domain.usecase.SongPlaybackUnavailableException
import javax.inject.Inject

/**
 * Decide cómo resolver una canción antes de reproducirla.
 *
 * Si el item tiene hlsMasterKey y un id válido, usa el camino conocido.
 * Si solo tiene hlsMasterKey, usa el camino desconocido del módulo song.
 */
class DefaultPlaybackSongResolver @Inject constructor(
    private val getSongUseCase: GetSongUseCase,
    private val refreshStreamingUrlUseCase: RefreshStreamingUrlUseCase,
    private val resolvedCache: PlaybackResolvedSongCache
) : PlaybackSongResolver {

    override suspend fun resolve(item: PlaybackQueueItem): Result<Song> {
        val hlsMasterKey = item.hlsMasterKey.trim()
        if (hlsMasterKey.isEmpty()) {
            return Result.failure(IllegalArgumentException("Missing hlsMasterKey"))
        }

        resolvedCache.get(hlsMasterKey)?.let { cached ->
            return Result.success(cached)
        }

        val songId = item.songId?.takeIf { it > 0L }

        val result = if (songId != null) {
            refreshStreamingUrlUseCase(hlsMasterKey, songId)
        } else {
            getSongUseCase(hlsMasterKey)
        }

        return result.ensurePlayable(hlsMasterKey).also { ensured ->
            ensured.onSuccess(resolvedCache::put)
        }
    }

    private fun Result<Song>.ensurePlayable(hlsMasterKey: String): Result<Song> =
        fold(
            onSuccess = { song ->
                if (song.streamingUrl.isNullOrBlank()) {
                    Result.failure(SongPlaybackUnavailableException(hlsMasterKey))
                } else {
                    Result.success(song)
                }
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
}