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
    private val resolutionJobs = mutableMapOf<String, Job>()
    private var bytesPrefetchedFor: String? = null

    fun prefetchNext(nextItem: PlaybackQueueItem?) {
        prefetchUpcoming(listOfNotNull(nextItem))
    }

    fun prefetchUpcoming(upcoming: List<PlaybackQueueItem>) {
        val targetLookups = upcoming.map { it.songLookup }.toSet()
        val obsolete = resolutionJobs.keys - targetLookups
        obsolete.forEach { lookup ->
            resolutionJobs.remove(lookup)?.cancel()
        }

        if (upcoming.isEmpty()) {
            cancelBytesPrefetchIfNotIn(emptySet())
            return
        }

        val immediate = upcoming.firstOrNull()
        if (immediate != null) {
            ensureBytesPrefetched(immediate)
        }

        upcoming.forEach { item ->
            ensureMetadataResolved(item, isImmediate = item === immediate)
        }
    }

    fun cancelAll() {
        resolutionJobs.values.forEach { it.cancel() }
        resolutionJobs.clear()
        mediaPrefetcher.cancel()
        bytesPrefetchedFor = null
    }

    private fun ensureMetadataResolved(item: PlaybackQueueItem, isImmediate: Boolean) {
        val cached = resolvedCache.get(item.songLookup)
        if (cached != null && !urlRefresher.isExpired(cached)) {
            if (isImmediate) ensureBytesPrefetched(item, cached)
            return
        }

        if (resolutionJobs[item.songLookup]?.isActive == true) return

        val job = scope.launch {
            try {
                val result = if (cached != null) {
                    urlRefresher.refreshIfExpired(cached) { true }
                } else {
                    getSongUseCase(item.songLookup, item.toLookupHint())
                }
                result.onSuccess { song ->
                    resolvedCache.put(item.songLookup, song)
                    Log.d(TAG, "Prefetch metadata OK: ${item.songLookup}")
                    if (isImmediate) ensureBytesPrefetched(item, song)
                }
                result.onFailure { error ->
                    Log.w(TAG, "Prefetch fallo (no critico) ${item.songLookup}: ${error.message}")
                }
            } catch (ce: CancellationException) {
                throw ce
            } catch (e: Exception) {
                Log.w(TAG, "Prefetch exception (no critico) ${item.songLookup}: ${e.message}")
            }
        }
        resolutionJobs[item.songLookup] = job
        job.invokeOnCompletion {
            if (resolutionJobs[item.songLookup] === job) {
                resolutionJobs.remove(item.songLookup)
            }
        }
    }

    private fun ensureBytesPrefetched(item: PlaybackQueueItem, song: Song? = resolvedCache.get(item.songLookup)) {
        if (song == null) return
        val url = song.streamingUrl
        if (url.isNullOrBlank()) return
        if (bytesPrefetchedFor == item.songLookup) return
        cancelBytesPrefetchIfNotIn(setOf(item.songLookup))
        bytesPrefetchedFor = item.songLookup
        mediaPrefetcher.prefetch(url)
    }

    private fun cancelBytesPrefetchIfNotIn(allowed: Set<String>) {
        val current = bytesPrefetchedFor ?: return
        if (current in allowed) return
        mediaPrefetcher.cancel()
        bytesPrefetchedFor = null
    }
}
