package com.essence.essenceapp.feature.song.ui.playback.manager

import android.util.Log
import com.essence.essenceapp.feature.history.domain.model.History
import com.essence.essenceapp.feature.history.domain.usecase.AddSongHistoryUseCase
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

private const val TAG = "PLAYBACK_HISTORY"

@Singleton
class PlaybackHistoryRecorder @Inject constructor(
    private val addSongHistoryUseCase: AddSongHistoryUseCase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _historyRecorded = MutableSharedFlow<Long>(extraBufferCapacity = 4)
    val historyRecorded: SharedFlow<Long> = _historyRecorded.asSharedFlow()

    private var historyRecordedForCurrent = false

    fun resetForNewSong() {
        historyRecordedForCurrent = false
    }

    fun isAlreadyRecorded(): Boolean = historyRecordedForCurrent

    fun shouldRecordOnSwitch(positionMs: Long): Boolean = positionMs >= MIN_LISTEN_MS

    fun recordCompleted(songId: Long, durationListenedMs: Long) {
        record(
            songId = songId,
            durationListenedMs = durationListenedMs,
            completed = true,
            skipped = false
        )
    }

    fun recordSkipped(songId: Long, durationListenedMs: Long) {
        record(
            songId = songId,
            durationListenedMs = durationListenedMs,
            completed = false,
            skipped = true
        )
    }

    private fun record(
        songId: Long,
        durationListenedMs: Long,
        completed: Boolean,
        skipped: Boolean
    ) {
        historyRecordedForCurrent = true

        val durationListened = durationListenedMs.toInt()
        val history = History(
            playlistId = null,
            albumId = null,
            durationListenedMs = durationListened,
            completed = completed,
            skipped = skipped,
            skipPositionMs = if (skipped) durationListened else null,
            deviceType = "ANDROID"
        )

        scope.launch {
            try {
                val result = addSongHistoryUseCase(songId, history)
                result.onSuccess {
                    Log.d(
                        TAG,
                        "History recorded: songId=$songId completed=$completed skipped=$skipped"
                    )
                    _historyRecorded.emit(songId)
                }
                result.onFailure { error ->
                    Log.e(TAG, "Failed to record history: ${error.message}", error)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception recording history: ${e.message}", e)
            }
        }
    }

    companion object {
        // Tiempo minimo escuchado para que una cancion cuente al cambiar
        const val MIN_LISTEN_MS = 60_000L
    }
}
