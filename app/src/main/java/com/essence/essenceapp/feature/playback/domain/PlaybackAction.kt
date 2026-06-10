package com.essence.essenceapp.feature.playback.domain

/**
 * Acciones que pueden cambiar el estado de la reproducción.
 *
 * Cada acción representa una intención concreta del usuario o de la app:
 * abrir una cola, reproducir, pausar, moverse entre canciones, cambiar modos
 * o modificar la cola actual.
 */
sealed interface PlaybackAction {
    data class Open(val request: PlaybackOpenRequest) : PlaybackAction

    data object Play : PlaybackAction
    data object Pause : PlaybackAction
    data object TogglePlayPause : PlaybackAction
    data object Stop : PlaybackAction

    data class SeekTo(val positionMs: Long) : PlaybackAction
    data class SeekBy(val deltaMs: Long) : PlaybackAction

    data object Next : PlaybackAction
    data object Previous : PlaybackAction
    data class SkipTo(val index: Int) : PlaybackAction

    data class SetRepeatMode(val mode: PlaybackRepeatMode) : PlaybackAction
    data class SetShuffleMode(val mode: PlaybackShuffleMode) : PlaybackAction
    data object ToggleRepeat : PlaybackAction

    data class AddToQueueNext(val item: PlaybackQueueItem) : PlaybackAction
    data class AddToQueueEnd(val item: PlaybackQueueItem) : PlaybackAction
    data class RemoveFromQueue(val index: Int) : PlaybackAction
    data class MoveInQueue(val from: Int, val to: Int) : PlaybackAction
    data object ClearUpcoming : PlaybackAction

    data class ToggleLike(val songId: Long) : PlaybackAction

    data class SetCurrentLike(
        val songId: Long,
        val isLiked: Boolean
    ) : PlaybackAction
}