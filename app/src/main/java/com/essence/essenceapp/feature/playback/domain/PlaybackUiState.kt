package com.essence.essenceapp.feature.playback.domain

/**
 * Estado completo de reproducción para pantallas que necesitan verlo todo junto.
 *
 * Agrupa canción actual, cola, progreso, estado principal y modos activos en
 * una sola estructura cómoda para la UI. Internamente se forma combinando los
 * estados más pequeños del controlador.
 */
data class PlaybackUiState(
    val nowPlaying: NowPlayingInfo?,
    val queue: PlaybackQueue?,
    val position: PlaybackPositionInfo,
    val playbackState: PlaybackState,
    val repeatMode: PlaybackRepeatMode,
    val shuffleMode: PlaybackShuffleMode
) {
    val isPlaying: Boolean
        get() = playbackState is PlaybackState.Playing

    val isBuffering: Boolean
        get() = playbackState is PlaybackState.Buffering

    val isFailed: Boolean
        get() = playbackState is PlaybackState.Failed

    val hasContent: Boolean
        get() = nowPlaying != null

    companion object {
        val Empty = PlaybackUiState(
            nowPlaying = null,
            queue = null,
            position = PlaybackPositionInfo.Zero,
            playbackState = PlaybackState.Idle,
            repeatMode = PlaybackRepeatMode.OFF,
            shuffleMode = PlaybackShuffleMode.OFF
        )
    }
}