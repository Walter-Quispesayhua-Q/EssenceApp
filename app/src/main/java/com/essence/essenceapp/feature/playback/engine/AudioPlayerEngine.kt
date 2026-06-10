package com.essence.essenceapp.feature.playback.engine

import kotlinx.coroutines.flow.StateFlow

/**
 * Fachada principal del motor de audio.
 *
 * Expone las operaciones básicas para cargar una canción, controlar la
 * reproducción y observar el estado técnico del player sin que el resto de
 * playback dependa directamente de Media3 o ExoPlayer.
 */
interface AudioPlayerEngine {
    val state: StateFlow<AudioPlayerState>

    suspend fun warmUp()

    fun play(
        request: AudioPlayRequest,
        forceRestart: Boolean = false,
        autoPlay: Boolean = true
    )

    fun resume()

    fun pause()

    fun stop()

    fun seekTo(positionMs: Long)

    fun setRepeatOne(enabled: Boolean)

    fun clearSourceRefreshRequest()

    fun release()
}
