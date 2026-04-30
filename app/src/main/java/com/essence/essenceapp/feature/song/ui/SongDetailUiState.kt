package com.essence.essenceapp.feature.song.ui

import com.essence.essenceapp.feature.song.domain.model.Song
import com.essence.essenceapp.feature.song.ui.playback.PlaybackUiState
import com.essence.essenceapp.shared.playback.model.PlaybackQueueItem

sealed interface SongDetailUiState {
    data object Loading : SongDetailUiState

    data class LoadingNextSong(
        val title: String,
        val artistName: String,
        val imageKey: String?,
        val durationMs: Long = 0L,
        val playback: PlaybackUiState
    ) : SongDetailUiState

    data class Error(
        val message: String
    ) : SongDetailUiState

    data class Unavailable(
        val songTitle: String
    ) : SongDetailUiState

    data class Success(
        val song: Song,
        val playback: PlaybackUiState,
        val isLikeSubmitting: Boolean = false,
        val queueItems: List<PlaybackQueueItem> = emptyList(),
        val queueCurrentIndex: Int = -1
    ) : SongDetailUiState
}
