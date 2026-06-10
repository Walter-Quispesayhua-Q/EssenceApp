package com.essence.essenceapp.feature.playback.observers

import android.util.Log
import com.essence.essenceapp.core.di.ApplicationScope
import com.essence.essenceapp.feature.playback.domain.PlaybackController
import com.essence.essenceapp.feature.playback.domain.PlaybackState
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

private const val TAG = "PLAYBACK_TTFP"

/**
 * Observa el tiempo que tarda una canción en empezar a sonar.
 *
 * Solo registra métricas en logs. No cambia el estado del player ni decide
 * qué canción reproducir.
 */
@Singleton
class PlaybackTtfpObserver @Inject constructor(
    private val playbackController: PlaybackController,
    @ApplicationScope private val scope: CoroutineScope
) {
    private val timers = ConcurrentHashMap<String, Long>()

    fun start() {
        playbackController.nowPlaying
            .onEach { nowPlaying ->
                val key = nowPlaying?.item?.hlsMasterKey ?: return@onEach
                val previous = timers.putIfAbsent(key, System.currentTimeMillis())
                if (previous == null) {
                    Log.d(TAG, "Timer start: $key")
                }
            }
            .launchIn(scope)

        playbackController.playbackState
            .onEach { state ->
                if (state is PlaybackState.Playing) {
                    reportCurrent()
                }
            }
            .launchIn(scope)
    }

    fun clear() {
        timers.clear()
    }

    private fun reportCurrent() {
        val key = playbackController.nowPlaying.value
            ?.item
            ?.hlsMasterKey
            ?: return

        val startedAt = timers.remove(key) ?: return
        val ttfp = System.currentTimeMillis() - startedAt

        Log.d(TAG, "TTFP=${ttfp}ms for $key")
    }
}