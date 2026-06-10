package com.essence.essenceapp.feature.playback.observers

import android.util.Log
import com.essence.essenceapp.core.di.ApplicationScope
import com.essence.essenceapp.feature.history.domain.model.History
import com.essence.essenceapp.feature.history.domain.usecase.AddSongHistoryUseCase
import com.essence.essenceapp.feature.playback.domain.PlaybackController
import com.essence.essenceapp.feature.playback.domain.PlaybackState
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

private const val TAG = "PLAYBACK_HISTORY"
private const val MIN_LISTEN_MS = 60_000L

/**
 * Registra historial cuando una canción fue escuchada lo suficiente.
 *
 * Escucha el estado público de playback y envía el registro al módulo history.
 * No controla reproducción, no avanza cola y no modifica el estado del player.
 */
@Singleton
class PlaybackHistoryObserver @Inject constructor(
    private val playbackController: PlaybackController,
    private val addSongHistoryUseCase: AddSongHistoryUseCase,
    @ApplicationScope private val scope: CoroutineScope
) {
    private var currentSongId: Long? = null
    private var recordedForCurrent = false

    fun start() {
        playbackController.nowPlaying
            .onEach { nowPlaying ->
                val songId = nowPlaying?.item?.songId

                if (songId != currentSongId) {
                    currentSongId = songId
                    recordedForCurrent = false
                }
            }
            .launchIn(scope)

        combine(
            playbackController.nowPlaying,
            playbackController.position,
            playbackController.playbackState
        ) { nowPlaying, position, state ->
            HistorySnapshot(
                songId = nowPlaying?.item?.songId,
                positionMs = position.positionMs,
                durationMs = position.durationMs,
                isEnded = state is PlaybackState.Ended
            )
        }
            .distinctUntilChanged()
            .onEach(::maybeRecord)
            .launchIn(scope)
    }

    private fun maybeRecord(snapshot: HistorySnapshot) {
        val songId = snapshot.songId ?: return
        if (recordedForCurrent) return

        val shouldRecord = snapshot.positionMs >= MIN_LISTEN_MS || snapshot.isEnded
        if (!shouldRecord) return

        recordedForCurrent = true

        val completed = snapshot.isEnded
        val listenedMs = snapshot.positionMs
            .takeIf { it > 0L }
            ?: snapshot.durationMs

        record(
            songId = songId,
            durationListenedMs = listenedMs,
            completed = completed,
            skipped = false
        )
    }

    private fun record(
        songId: Long,
        durationListenedMs: Long,
        completed: Boolean,
        skipped: Boolean
    ) {
        val safeDuration = durationListenedMs
            .coerceAtLeast(0L)
            .coerceAtMost(Int.MAX_VALUE.toLong())
            .toInt()

        val history = History(
            playlistId = null,
            albumId = null,
            durationListenedMs = safeDuration,
            completed = completed,
            skipped = skipped,
            skipPositionMs = if (skipped) safeDuration else null,
            deviceType = "ANDROID"
        )

        scope.launch {
            val result = addSongHistoryUseCase(songId, history)

            result.onSuccess {
                Log.d(TAG, "History recorded songId=$songId completed=$completed skipped=$skipped")
            }

            result.onFailure { error ->
                Log.e(TAG, "History record failed songId=$songId: ${error.message}", error)
            }
        }
    }

    private data class HistorySnapshot(
        val songId: Long?,
        val positionMs: Long,
        val durationMs: Long,
        val isEnded: Boolean
    )
}