package com.essence.essenceapp.feature.song.ui.manager

sealed interface SongDetailManagerAction {
    data object Play : SongDetailManagerAction
    data object Pause : SongDetailManagerAction
    data object Stop : SongDetailManagerAction
    data object Next : SongDetailManagerAction
    data object Previous : SongDetailManagerAction
    data class SeekTo(val positionMs: Long) : SongDetailManagerAction
    data class SeekBy(val deltaMs: Long) : SongDetailManagerAction
}