package com.essence.essenceapp.feature.song.ui.playback.manager

import android.util.Log
import com.essence.essenceapp.core.streaming.GoogleVideoUrl
import com.essence.essenceapp.feature.song.domain.model.Song
import com.essence.essenceapp.feature.song.domain.usecase.RefreshStreamingUrlUseCase
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

private const val TAG = "PLAYBACK_REFRESH"
private val PROACTIVE_REFRESH_THRESHOLD: Duration = Duration.ofMinutes(30)

@Singleton
class PlaybackUrlRefresher @Inject constructor(
    private val refreshStreamingUrlUseCase: RefreshStreamingUrlUseCase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val forceRefreshDeferred = ConcurrentHashMap<String, Deferred<Result<Song>>>()
    private val proactiveJobs = ConcurrentHashMap<String, Job>()

    fun isExpired(song: Song): Boolean {
        val url = song.streamingUrl
        if (url.isNullOrBlank()) return true
        val expiresAt = song.streamingUrlExpiresAt
            ?: GoogleVideoUrl.expireFrom(url)
            ?: return false
        return expiresAt.isBefore(Instant.now())
    }

    suspend fun refreshIfExpired(
        song: Song,
        isStillCurrent: () -> Boolean
    ): Result<Song> {
        if (!isExpired(song)) return Result.success(song)
        return refreshStreamingUrlUseCase(
            currentSong = song,
            isStillCurrent = isStillCurrent
        )
    }

    suspend fun forceRefresh(
        song: Song,
        isStillCurrent: () -> Boolean
    ): Result<Song> {
        val videoId = song.hlsMasterKey
        forceRefreshDeferred[videoId]?.let { existing ->
            if (existing.isActive) {
                return existing.await()
            }
        }

        val deferred = scope.async {
            try {
                Log.d(TAG, "Force refresh: $videoId")
                refreshStreamingUrlUseCase(
                    currentSong = song,
                    isStillCurrent = isStillCurrent
                )
            } catch (ce: CancellationException) {
                throw ce
            } catch (e: Exception) {
                Log.e(TAG, "Force refresh exception $videoId: ${e.message}", e)
                Result.failure(e)
            }
        }
        forceRefreshDeferred[videoId] = deferred
        return try {
            deferred.await()
        } finally {
            forceRefreshDeferred.remove(videoId)
        }
    }

    fun scheduleProactive(
        song: Song,
        onRefreshed: (Song) -> Unit
    ) {
        val videoId = song.hlsMasterKey
        if (proactiveJobs.containsKey(videoId)) return
        val expiresAt = song.streamingUrlExpiresAt ?: return
        val remaining = Duration.between(Instant.now(), expiresAt)
        if (remaining.isNegative || remaining.isZero) return
        if (remaining > PROACTIVE_REFRESH_THRESHOLD) return

        proactiveJobs[videoId] = scope.launch {
            try {
                Log.d(TAG, "Proactive refresh: $videoId (remaining=${remaining.toMinutes()}min)")
                val result = refreshStreamingUrlUseCase(
                    currentSong = song,
                    isStillCurrent = { true }
                )
                result.onSuccess { fresh ->
                    Log.d(TAG, "Proactive refresh OK: $videoId")
                    onRefreshed(fresh)
                }
                result.onFailure { error ->
                    Log.w(TAG, "Proactive refresh fallo (no critico) $videoId: ${error.message}")
                }
            } catch (ce: CancellationException) {
                throw ce
            } catch (e: Exception) {
                Log.w(TAG, "Proactive refresh exception (no critico) $videoId: ${e.message}")
            } finally {
                proactiveJobs.remove(videoId)
            }
        }
    }
}
