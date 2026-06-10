package com.essence.essenceapp.feature.playback.domain

import com.essence.essenceapp.feature.song.domain.model.Song
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Contrato principal para observar y controlar la reproducción.
 *
 * Desde aquí se consulta la canción actual, la cola, el progreso, los modos
 * de reproducción y los errores. También recibe las acciones del usuario,
 * como reproducir, pausar, saltar de canción o abrir una nueva cola.
 */
interface PlaybackController {
    val nowPlaying: StateFlow<NowPlayingInfo?>
    val currentSong: StateFlow<Song?>
    val queue: StateFlow<PlaybackQueue?>
    val position: StateFlow<PlaybackPositionInfo>
    val playbackState: StateFlow<PlaybackState>
    val repeatMode: StateFlow<PlaybackRepeatMode>
    val shuffleMode: StateFlow<PlaybackShuffleMode>
    val errors: SharedFlow<PlaybackError>
    val uiState: StateFlow<PlaybackUiState>

    fun dispatch(action: PlaybackAction)
}