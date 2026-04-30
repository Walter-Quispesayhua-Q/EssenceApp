package com.essence.essenceapp.feature.song.ui.playback

sealed interface PlaybackAction {
    data object Play : PlaybackAction
    data object Pause : PlaybackAction
    data object Stop : PlaybackAction
    data object Next : PlaybackAction
    data object Previous : PlaybackAction
    data class SeekTo(val positionMs: Long) : PlaybackAction
    data class SeekBy(val deltaMs: Long) : PlaybackAction
    data object ToggleRepeat : PlaybackAction
    data object ToggleLike : PlaybackAction
}