package com.essence.essenceapp.feature.song.data.repository

import android.util.Log
import com.essence.essenceapp.feature.song.data.api.SongApiService
import com.essence.essenceapp.feature.song.data.mapper.songToDomain
import com.essence.essenceapp.feature.song.data.repository.resolver.known.KnownSongResolver
import com.essence.essenceapp.feature.song.data.repository.resolver.unknown.UnknownSongResolver
import com.essence.essenceapp.feature.song.domain.model.Song
import com.essence.essenceapp.feature.song.domain.repository.SongRepository
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async

class SongRepositoryImpl(
    private val apiService: SongApiService,
    private val unknownSongResolver: UnknownSongResolver,
    private val knownSongResolver: KnownSongResolver
) : SongRepository {

    private val inFlightRequests = ConcurrentHashMap<String, Deferred<Song?>>()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override suspend fun getSong(hlsMasterKey: String, forceRefresh: Boolean): Song? {
        val key = if (forceRefresh) "$hlsMasterKey|refresh" else hlsMasterKey
        return deduplicate(key) {
            apiService.getSong(hlsMasterKey, forceRefresh)?.songToDomain()
        }
    }

    override suspend fun syncSong(hlsMasterKey: String): Song? =
        deduplicate("resolve|unknown|$hlsMasterKey") {
            unknownSongResolver
                .resolve(hlsMasterKey)
                .toSongOrNull(TAG, "resolve unknown [$hlsMasterKey]")
        }

    override suspend fun refreshStreamingUrl(
        hlsMasterKey: String,
        songId: Long
    ): Song? =
        deduplicate("refresh|known|$hlsMasterKey|$songId") {
            knownSongResolver
                .resolve(hlsMasterKey, songId)
                .toSongOrNull(TAG, "refresh streaming URL [$hlsMasterKey/$songId]")
        }

    override suspend fun addLikeSong(songId: Long) {
        apiService.addLikeSong(songId)
    }

    override suspend fun deleteLikeSong(songId: Long) {
        apiService.deleteLikeSong(songId)
    }

    private suspend fun deduplicate(key: String, block: suspend () -> Song?): Song? {
        val deferred = inFlightRequests.computeIfAbsent(key) {
            scope.async {
                try {
                    block()
                } catch (ce: CancellationException) {
                    throw ce
                } catch (e: Exception) {
                    Log.e(TAG, "Repository operation $key failed: ${e.message}")
                    null
                } finally {
                    inFlightRequests.remove(key)
                }
            }
        }
        return deferred.await()
    }

    companion object {
        private const val TAG = "SongRepository"
    }
}