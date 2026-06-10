package com.essence.essenceapp.feature.playback.engine.mediacontroller

import android.util.Log
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.essence.essenceapp.feature.playback.engine.AudioPlayRequest
import com.essence.essenceapp.feature.playback.engine.AudioPlayerEngine
import com.essence.essenceapp.feature.playback.engine.AudioPlayerError
import com.essence.essenceapp.feature.playback.engine.AudioPlayerState
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val TAG = "AUDIO_ENGINE"

/**
 * Implementacion del motor de audio usando MediaController.
 *
 * Coordina la conexion con MediaSession, ejecuta comandos de reproduccion y
 * publica un estado tecnico simple para el controller de playback.
 */
@Singleton
class MediaControllerAudioPlayerEngine @Inject constructor(
    private val connection: MediaControllerConnection,
    private val commands: MediaControllerPlaybackCommands,
    private val stateMapper: AudioPlayerStateMapper,
    private val errorMapper: AudioPlayerErrorMapper,
    private val positionTracker: AudioPositionTracker
) : AudioPlayerEngine {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val commandMutex = Mutex()

    private val _state = MutableStateFlow(AudioPlayerState())
    override val state: StateFlow<AudioPlayerState> = _state.asStateFlow()

    private var currentRequest: AudioPlayRequest? = null

    override suspend fun warmUp() {
        runCatching { connection.warmUp() }
    }

    private val listener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            val controller = connection.current() ?: return
            updateState(controller)

            if (playbackState == Player.STATE_ENDED) {
                positionTracker.stop()
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            val controller = connection.current() ?: return
            updateState(controller)

            if (isPlaying) {
                positionTracker.start(scope, controller, ::updateState)
            } else {
                positionTracker.stop()
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            handlePlayerError(error)
        }
    }

    override fun play(
        request: AudioPlayRequest,
        forceRestart: Boolean,
        autoPlay: Boolean
    ) {
        runCommand { controller ->
            val previousRequest = currentRequest
            currentRequest = request

            _state.value = AudioPlayerState(
                mediaId = request.mediaId,
                isBuffering = true,
                positionMs = request.startPositionMs
            )

            commands.play(
                controller = controller,
                request = request,
                previousRequest = previousRequest,
                forceRestart = forceRestart,
                autoPlay = autoPlay
            )

            updateState(controller)
        }
    }

    override fun resume() {
        runCommand { controller ->
            _state.value = _state.value.copy(error = null)

            commands.resume(controller)
            updateState(controller)
        }
    }

    override fun pause() {
        runCommand { controller ->
            commands.pause(controller)
            updateState(controller)
        }
    }

    override fun stop() {
        runCommand { controller ->
            positionTracker.stop()

            commands.stop(controller)

            currentRequest = null
            _state.value = AudioPlayerState()
        }
    }

    override fun seekTo(positionMs: Long) {
        runCommand { controller ->
            commands.seekTo(controller, positionMs)
            updateState(controller)
        }
    }

    override fun setRepeatOne(enabled: Boolean) {
        runCommand { controller ->
            commands.setRepeatOne(controller, enabled)
            updateState(controller)
        }
    }

    override fun clearSourceRefreshRequest() {
        _state.value = _state.value.copy(
            requiresSourceRefresh = false,
            error = null
        )
    }

    override fun release() {
        positionTracker.stop()
        connection.release(listener)

        currentRequest = null
        _state.value = AudioPlayerState()
    }

    private fun runCommand(
        block: suspend (MediaController) -> Unit
    ) {
        scope.launch {
            try {
                commandMutex.withLock {
                    block(connection.get(listener))
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.e(TAG, "Audio command failed: ${error.message}", error)

                _state.value = _state.value.copy(
                    isBuffering = false,
                    error = AudioPlayerError.Unknown(
                        message = error.message ?: "No se pudo controlar el reproductor.",
                        cause = error
                    )
                )
            }
        }
    }

    private fun updateState(controller: MediaController) {
        _state.value = stateMapper.map(
            controller = controller,
            currentRequest = currentRequest,
            previous = _state.value
        )
    }

    private fun handlePlayerError(error: PlaybackException) {
        Log.e(TAG, "Player error: ${error.errorCodeName} ${error.message}", error)

        positionTracker.stop()

        val mappedError = errorMapper.map(error)

        _state.value = _state.value.copy(
            isPlaying = false,
            isBuffering = false,
            requiresSourceRefresh = mappedError is AudioPlayerError.SourceExpired ||
                mappedError is AudioPlayerError.Decoder,
            error = mappedError
        )
    }
}
