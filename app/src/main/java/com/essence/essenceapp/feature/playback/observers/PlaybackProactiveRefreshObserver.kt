package com.essence.essenceapp.feature.playback.observers

import android.util.Log
import com.essence.essenceapp.core.di.ApplicationScope
import com.essence.essenceapp.core.streaming.ExtractorTimeStreamUrl
import com.essence.essenceapp.feature.playback.domain.PlaybackController
import com.essence.essenceapp.feature.playback.manager.resolver.PlaybackResolvedSongCache
import com.essence.essenceapp.feature.song.domain.model.Song
import com.essence.essenceapp.feature.song.domain.usecase.RefreshStreamingUrlUseCase
import java.time.Clock
import java.time.Duration
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch

private const val TAG = "PLAYBACK_REFRESH"

/**
 * Mantiene fresca la URL de la cancion actual antes de que expire.
 *
 * No cambia el MediaItem que ya esta sonando. Solo actualiza el cache de Song
 * resuelta para que, si Media3 dispara recovery por URL vencida, playback
 * encuentre una URL fresca sin volver a pagar el coste completo de NewPipe.
 */
@Singleton
class PlaybackProactiveRefreshObserver @Inject constructor(
    private val playbackController: PlaybackController,
    private val refreshStreamingUrlUseCase: RefreshStreamingUrlUseCase,
    private val resolvedSongCache: PlaybackResolvedSongCache,
    private val clock: Clock,
    @ApplicationScope private val scope: CoroutineScope
) {
    fun start() {
        playbackController.currentSong
            .distinctUntilChangedBy { song ->
                RefreshKey(
                    hlsMasterKey = song?.hlsMasterKey,
                    songId = song?.id,
                    expiresAt = song?.resolvedExpiresAt()
                )
            }
            .collectLatestInScope { song ->
                scheduleRefresh(song ?: return@collectLatestInScope)
            }
    }

    private suspend fun scheduleRefresh(song: Song) {
        val key = song.hlsMasterKey.takeIf { it.isNotBlank() } ?: return
        val songId = song.id.takeIf { it > 0L } ?: return
        val expiresAt = song.resolvedExpiresAt() ?: return

        val refreshAt = expiresAt.minus(REFRESH_THRESHOLD)
        val waitMs = Duration.between(clock.instant(), refreshAt)
            .toMillis()
            .coerceAtLeast(0L)

        if (waitMs > 0L) {
            Log.d(
                TAG,
                "proactive[$key] scheduled in ${waitMs}ms " +
                    "(expiresAt=$expiresAt)"
            )
            delay(waitMs)
        }

        val current = playbackController.currentSong.value
        if (current?.hlsMasterKey != key || current.id != songId) {
            Log.d(TAG, "proactive[$key] skipped (no longer current)")
            return
        }

        val currentExpiresAt = current.resolvedExpiresAt()
        if (currentExpiresAt != null && currentExpiresAt.isAfter(expiresAt)) {
            Log.d(TAG, "proactive[$key] skipped (newer URL already present)")
            return
        }

        Log.d(TAG, "proactive[$key] refreshing (expiresAt=$expiresAt)")
        val result = refreshStreamingUrlUseCase(key, songId)
        result.onSuccess { fresh ->
            resolvedSongCache.put(fresh)
            Log.d(
                TAG,
                "proactive[$key] OK (expiresAt=${fresh.resolvedExpiresAt()})"
            )
        }
        result.onFailure { error ->
            Log.w(TAG, "proactive[$key] failed: ${error.message}")
        }
    }

    private fun Song.resolvedExpiresAt(): Instant? =
        streamingUrlExpiresAt ?: ExtractorTimeStreamUrl.expireFrom(streamingUrl)

    private fun <T> kotlinx.coroutines.flow.Flow<T>.collectLatestInScope(
        block: suspend (T) -> Unit
    ) {
        scope.launch {
            try {
                collectLatest(block)
            } catch (ce: CancellationException) {
                throw ce
            } catch (error: Exception) {
                Log.e(TAG, "proactive observer stopped: ${error.message}", error)
            }
        }
    }

    private data class RefreshKey(
        val hlsMasterKey: String?,
        val songId: Long?,
        val expiresAt: Instant?
    )

    private companion object {
        val REFRESH_THRESHOLD: Duration = Duration.ofSeconds(60)
    }
}
