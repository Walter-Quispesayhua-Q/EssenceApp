package com.essence.essenceapp.feature.song.data.repository

import android.util.Log
import com.essence.essenceapp.core.extractor.SongExtractor
import com.essence.essenceapp.core.extractor.toSyncRequestDTO
import com.essence.essenceapp.feature.song.data.api.SongApiService
import com.essence.essenceapp.feature.song.data.mapper.songToDomain
import com.essence.essenceapp.feature.song.domain.model.Song
import com.essence.essenceapp.feature.song.domain.repository.SongRepository
import com.essence.essenceapp.shared.streaming.StreamingUrlSyncManager
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.withTimeoutOrNull
import retrofit2.HttpException

class SongRepositoryImpl(
    private val apiService: SongApiService,
    private val streamingUrlSyncManager: StreamingUrlSyncManager
) : SongRepository {

    private val inFlightRequests = ConcurrentHashMap<String, Deferred<Song?>>()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override suspend fun getSong(songLookup: String, forceRefresh: Boolean): Song? {
        val key = if (forceRefresh) "$songLookup|refresh" else songLookup

        val deferred = inFlightRequests.computeIfAbsent(key) {
            scope.async {
                try {
                    val apiDTO = apiService.getSong(songLookup, forceRefresh)
                    apiDTO?.songToDomain()
                } finally {
                    inFlightRequests.remove(key)
                }
            }
        }
        return deferred.await()
    }

    override suspend fun syncSong(videoId: String): Song? {
        val key = "sync|$videoId"

        val deferred = inFlightRequests.computeIfAbsent(key) {
            scope.async {
                try {
                    val cachedInBackend = tryFetchFromBackend(videoId)
                    if (cachedInBackend != null) {
                        Log.d(TAG, "GET-first hit: $videoId")
                        return@async cachedInBackend
                    }

                    Log.d(TAG, "GET-first miss, falling back to NewPipe + POST: $videoId")
                    val extracted = withTimeoutOrNull(EXTRACTION_TIMEOUT_MS) {
                        SongExtractor.extract(videoId)
                    }

                    if (extracted != null) {
                        Log.d(TAG, "Client extraction succeeded for: $videoId")
                        val response = apiService.syncSong(extracted.toSyncRequestDTO())
                        response?.songToDomain()
                    } else {
                        Log.w(TAG, "Client extraction failed, asking backend to extract: $videoId")
                        val response = apiService.getSong(videoId, forceRefresh = true)
                        response?.songToDomain()
                    }
                } catch (ce: CancellationException) {
                    throw ce
                } catch (e: Exception) {
                    Log.e(TAG, "syncSong error for $videoId: ${e.message}")
                    try {
                        apiService.getSong(videoId, forceRefresh = true)?.songToDomain()
                    } catch (ce: CancellationException) {
                        throw ce
                    } catch (_: Exception) {
                        null
                    }
                } finally {
                    inFlightRequests.remove(key)
                }
            }
        }
        return deferred.await()
    }

    private suspend fun tryFetchFromBackend(videoId: String): Song? {
        return try {
            apiService.getSong(videoId, forceRefresh = false)?.songToDomain()
        } catch (ce: CancellationException) {
            throw ce
        } catch (e: HttpException) {
            if (e.code() == 404) {
                Log.d(TAG, "Backend 404 for $videoId")
            } else {
                Log.w(TAG, "Backend HTTP ${e.code()} for $videoId: ${e.message}")
            }
            null
        } catch (e: Exception) {
            Log.w(TAG, "Backend unreachable for $videoId: ${e.message}")
            null
        }
    }

    override suspend fun refreshStreamingUrl(
        currentSong: Song,
        isStillCurrent: () -> Boolean
    ): Song? {
        val videoId = currentSong.hlsMasterKey
        val key = "refresh|$videoId"

        val deferred = inFlightRequests.computeIfAbsent(key) {
            scope.async {
                try {
                    val newUrl = withTimeoutOrNull(EXTRACTION_TIMEOUT_MS) {
                        SongExtractor.extractStreamingUrl(videoId)
                    }

                    if (newUrl != null) {
                        Log.d(TAG, "URL refresh local OK: $videoId")
                        val refreshed = currentSong.copy(
                            streamingUrl = newUrl,
                            streamingUrlExpiresAt = Instant.now()
                                .plus(URL_TTL_HOURS, ChronoUnit.HOURS)
                        )
                        streamingUrlSyncManager.schedule(
                            videoId = videoId,
                            freshUrl = newUrl,
                            isStillCurrent = isStillCurrent
                        )
                        refreshed
                    } else {
                        Log.w(TAG, "URL refresh local failed, fallback backend: $videoId")
                        apiService.getSong(videoId, forceRefresh = true)?.songToDomain()
                    }
                } catch (ce: CancellationException) {
                    throw ce
                } catch (e: Exception) {
                    Log.e(TAG, "refreshStreamingUrl error for $videoId: ${e.message}")
                    try {
                        apiService.getSong(videoId, forceRefresh = true)?.songToDomain()
                    } catch (ce: CancellationException) {
                        throw ce
                    } catch (_: Exception) {
                        null
                    }
                } finally {
                    inFlightRequests.remove(key)
                }
            }
        }
        return deferred.await()
    }

    override suspend fun addLikeSong(songId: Long) {
        apiService.addLikeSong(songId)
    }

    override suspend fun deleteLikeSong(songId: Long) {
        apiService.deleteLikeSong(songId)
    }

    companion object {
        private const val TAG = "SongRepository"

        private const val EXTRACTION_TIMEOUT_MS = 8_000L
        private const val URL_TTL_HOURS = 5L
    }
}