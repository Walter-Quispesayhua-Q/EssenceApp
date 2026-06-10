package com.essence.essenceapp.feature.playback.prefetch

import android.util.Log
import com.essence.essenceapp.feature.playback.domain.PlaybackQueue
import com.essence.essenceapp.feature.playback.domain.PlaybackQueueItem
import com.essence.essenceapp.feature.playback.domain.PlaybackState
import com.essence.essenceapp.feature.playback.manager.PlaybackStateStore
import com.essence.essenceapp.feature.playback.manager.resolver.PlaybackSongResolver
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

private const val TAG = "PLAYBACK_PREFETCH"

/**
 * Decide qué canción próxima conviene preparar en cache.
 *
 * Usa el resolver de playback para obtener una canción con URL fresca y luego
 * delega la descarga de bytes a MediaPrefetcher. No descarga por sí mismo.
 */
@Singleton
class PlaybackPrefetchCoordinator @Inject constructor(
    private val mediaPrefetcher: MediaPrefetcher,
    private val playbackSongResolver: PlaybackSongResolver,
    private val stateStore: PlaybackStateStore
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var resolutionJob: Job? = null
    private var armedTargetKey: String? = null
    private var pendingTarget: PlaybackQueueItem? = null

    init {
        stateStore.playbackState
            .filter { it == PlaybackState.Playing }
            .onEach { runPendingPrefetch() }
            .launchIn(scope)
    }

    fun prefetchNext(queue: PlaybackQueue?) {
        val nextItem = queue?.upcoming?.firstOrNull()

        if (nextItem == null) {
            cancel()
            return
        }

        prefetch(nextItem)
    }

    fun prefetch(item: PlaybackQueueItem?) {
        if (item == null) {
            cancel()
            return
        }

        val key = item.hlsMasterKey
        if (key.isBlank()) return
        if (armedTargetKey == key && resolutionJob?.isActive == true) return

        if (armedTargetKey != null && armedTargetKey != key) {
            cancel()
        }

        pendingTarget = item
        Log.d(TAG, "Prefetch armed key=$key (state=${stateStore.playbackState.value})")

        if (isCurrentStable()) {
            runPendingPrefetch()
        }
    }

    private fun isCurrentStable(): Boolean =
        stateStore.playbackState.value is PlaybackState.Playing

    private fun runPendingPrefetch() {
        val target = pendingTarget ?: return
        val key = target.hlsMasterKey
        if (key.isBlank()) return
        if (armedTargetKey == key && resolutionJob?.isActive == true) return

        pendingTarget = null
        armedTargetKey = key

        resolutionJob = scope.launch {
            try {
                Log.d(TAG, "Prefetch resolve start key=$key")

                val result = playbackSongResolver.resolve(target)

                result.onSuccess { song ->
                    val url = song.streamingUrl

                    if (url.isNullOrBlank()) {
                        Log.w(TAG, "Prefetch skipped key=$key reason=no_url")
                        return@onSuccess
                    }

                    if (armedTargetKey != key) {
                        Log.d(TAG, "Prefetch skipped key=$key reason=obsolete")
                        return@onSuccess
                    }

                    Log.d(TAG, "Prefetch resolved key=$key")
                    mediaPrefetcher.prefetch(
                        key = key,
                        url = url
                    )
                }

                result.onFailure { error ->
                    Log.w(TAG, "Prefetch resolve failed key=$key: ${error.message}")
                }
            } catch (ce: CancellationException) {
                Log.d(TAG, "Prefetch resolve cancelled key=$key")
                throw ce
            } catch (error: Exception) {
                Log.w(TAG, "Prefetch resolve exception key=$key: ${error.message}")
            }
        }
    }

    fun cancel() {
        resolutionJob?.cancel()
        resolutionJob = null
        armedTargetKey = null
        pendingTarget = null
        mediaPrefetcher.cancel()
    }
}
