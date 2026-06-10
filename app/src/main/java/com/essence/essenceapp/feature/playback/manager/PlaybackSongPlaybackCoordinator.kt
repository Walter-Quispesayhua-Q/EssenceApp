package com.essence.essenceapp.feature.playback.manager

import com.essence.essenceapp.core.di.ApplicationScope
import com.essence.essenceapp.feature.playback.domain.PlaybackError
import com.essence.essenceapp.feature.playback.domain.PlaybackPositionInfo
import com.essence.essenceapp.feature.playback.domain.PlaybackQueueItem
import com.essence.essenceapp.feature.playback.domain.PlaybackState
import com.essence.essenceapp.feature.playback.engine.AudioPlayRequest
import com.essence.essenceapp.feature.playback.engine.AudioPlayerEngine
import com.essence.essenceapp.feature.playback.manager.resolver.PlaybackSongResolver
import com.essence.essenceapp.feature.song.domain.model.Song
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Prepara y reproduce la cancion actual.
 *
 * Pide al resolver una Song con URL reproducible, crea el pedido para el
 * engine y lo manda a sonar. No decide cola ni maneja botones de UI.
 */
@Singleton
class PlaybackSongPlaybackCoordinator @Inject constructor(
    private val stateStore: PlaybackStateStore,
    private val playbackSongResolver: PlaybackSongResolver,
    private val audioPlayerEngine: AudioPlayerEngine,
    @ApplicationScope private val scope: CoroutineScope
) {
    private var playVersion: Long = 0L

    fun invalidate() {
        playVersion++
        stateStore.clearCurrentResolvedSong()
    }

    fun playCurrent(
        startPositionMs: Long,
        forceRestart: Boolean,
        autoPlay: Boolean = true
    ): Job? {
        val item = stateStore.currentItem

        if (item == null) {
            stateStore.tryFail(PlaybackError.QueueEmpty)
            return null
        }

        val version = ++playVersion

        stateStore.clearCurrentResolvedSong()
        stateStore.resetPosition(startPositionMs)
        stateStore.setPlaybackState(PlaybackState.Buffering)
        stateStore.updateNowPlaying()

        return scope.launch {
            launch { audioPlayerEngine.warmUp() }

            val result = playbackSongResolver.resolve(item)

            if (version != playVersion) return@launch

            result.fold(
                onSuccess = { song ->
                    val request = song.toAudioPlayRequest(
                        fallbackItem = item,
                        startPositionMs = startPositionMs
                    )

                    if (request == null) {
                        stateStore.fail(
                            PlaybackError.ResolveFailed(
                                hlsMasterKey = item.hlsMasterKey,
                                songId = item.songId,
                                message = "La cancion no tiene una URL reproducible."
                            )
                        )
                        return@launch
                    }

                    stateStore.setCurrentResolvedSong(song)

                    audioPlayerEngine.play(
                        request = request,
                        forceRestart = forceRestart,
                        autoPlay = autoPlay
                    )
                },
                onFailure = { error ->
                    stateStore.fail(
                        PlaybackError.ResolveFailed(
                            hlsMasterKey = item.hlsMasterKey,
                            songId = item.songId,
                            message = error.message ?: "No se pudo preparar la cancion."
                        )
                    )
                }
            )
        }
    }

    private fun Song.toAudioPlayRequest(
        fallbackItem: PlaybackQueueItem,
        startPositionMs: Long
    ): AudioPlayRequest? {
        val url = streamingUrl?.takeIf { it.isNotBlank() } ?: return null

        val resolvedArtistName = artists
            .joinToString(separator = ", ") { it.nameArtist }
            .ifBlank { fallbackItem.artistName }

        return AudioPlayRequest(
            url = url,
            mediaId = fallbackItem.hlsMasterKey,
            title = title.ifBlank { fallbackItem.title },
            artistName = resolvedArtistName,
            artworkUri = imageKey ?: fallbackItem.imageKey,
            startPositionMs = startPositionMs
        )
    }
}
