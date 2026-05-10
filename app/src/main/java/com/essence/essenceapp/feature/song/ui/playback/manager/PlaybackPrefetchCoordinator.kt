package com.essence.essenceapp.feature.song.ui.playback.manager

import android.util.Log
import com.essence.essenceapp.feature.song.domain.model.Song
import com.essence.essenceapp.feature.song.domain.usecase.GetSongUseCase
import com.essence.essenceapp.feature.song.ui.playback.engine.MediaPrefetcher
import com.essence.essenceapp.shared.playback.mapper.toLookupHint
import com.essence.essenceapp.shared.playback.model.PlaybackQueueItem
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

private const val TAG = "PLAYBACK_PREFETCH"

@Singleton
class PlaybackPrefetchCoordinator @Inject constructor(
    private val mediaPrefetcher: MediaPrefetcher,
    private val getSongUseCase: GetSongUseCase,
    private val urlRefresher: PlaybackUrlRefresher,
    private val resolvedCache: ResolvedSongCache
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var prefetchJob: Job? = null

    fun prefetchNext(nextItem: PlaybackQueueItem?) {
        if (nextItem == null) return

        prefetchJob?.cancel()
        prefetchJob = null

        val cached = resolvedCache.get(nextItem.songLookup)
        if (cached != null && !urlRefresher.isExpired(cached)) {
            prefetchAudio(cached)
            return
        }

        prefetchJob = scope.launch {
            try {
                val result = if (cached != null) {
                    urlRefresher.refreshIfExpired(cached) { true }
                } else {
                    getSongUseCase(nextItem.songLookup, nextItem.toLookupHint())
                }
                result.onSuccess { song ->
                    resolvedCache.put(nextItem.songLookup, song)
                    Log.d(TAG, "Prefetch OK: ${nextItem.songLookup}")
                    prefetchAudio(song)
                }
                result.onFailure { error ->
                    Log.w(TAG, "Prefetch fallo (no critico) ${nextItem.songLookup}: ${error.message}")
                }
            } catch (ce: CancellationException) {
                throw ce
            } catch (e: Exception) {
                Log.w(TAG, "Prefetch exception (no critico) ${nextItem.songLookup}: ${e.message}")
            }
        }
    }

    fun cancelAll() {
        prefetchJob?.cancel()
        prefetchJob = null
        mediaPrefetcher.cancel()
    }

    private fun prefetchAudio(song: Song) {
        val url = song.streamingUrl ?: return
        if (url.isBlank()) return
        mediaPrefetcher.prefetch(url)
    }
}
