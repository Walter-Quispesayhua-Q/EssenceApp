package com.essence.essenceapp.feature.playback.manager

import com.essence.essenceapp.feature.playback.domain.PlaybackPositionInfo
import com.essence.essenceapp.feature.playback.domain.PlaybackState
import com.essence.essenceapp.feature.playback.engine.AudioPlayerState
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interpreta el estado tecnico que emite el engine.
 *
 * Actualiza posicion, buffering, playing, ended y errores sin mezclar esa
 * lectura de Media3 dentro del controller principal.
 */
@Singleton
class PlaybackEngineStateHandler @Inject constructor(
    private val stateStore: PlaybackStateStore,
    private val errorMapper: PlaybackErrorMapper
) {
    private var handledEndedMediaId: String? = null

    fun resetEndedMarker() {
        handledEndedMediaId = null
    }

    suspend fun handle(
        state: AudioPlayerState,
        onRefreshRequired: () -> Boolean,
        onEnded: (String?) -> Unit
    ) {
        val currentItem = stateStore.currentItem

        if (
            state.mediaId != null &&
            currentItem != null &&
            state.mediaId != currentItem.hlsMasterKey
        ) {
            return
        }

        stateStore.setPosition(
            PlaybackPositionInfo(
                positionMs = state.positionMs,
                durationMs = state.durationMs,
                bufferedMs = state.bufferedMs
            )
        )

        if (state.requiresSourceRefresh) {
            if (onRefreshRequired()) return

            state.error?.let { error ->
                stateStore.fail(errorMapper.map(error, currentItem))
            }
            return
        }

        state.error?.let { error ->
            stateStore.fail(errorMapper.map(error, currentItem))
            return
        }

        if (state.hasEnded) {
            if (state.mediaId != null && handledEndedMediaId == state.mediaId) return

            handledEndedMediaId = state.mediaId
            onEnded(state.mediaId)
            return
        }

        stateStore.setPlaybackState(
            when {
                state.isBuffering -> PlaybackState.Buffering
                state.isPlaying -> PlaybackState.Playing
                currentItem != null -> PlaybackState.Paused
                else -> PlaybackState.Idle
            }
        )
    }
}
