package com.essence.essenceapp.feature.playlist.ui.detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import com.essence.essenceapp.feature.playlist.ui.detail.componets.PlaylistDetailContent
import com.essence.essenceapp.shared.playback.mapper.toQueueItems
import com.essence.essenceapp.shared.playback.model.PlaybackOpenRequest

@Composable
fun PlaylistDetailScreen(
    playlistId: Long,
    viewModel: PlaylistDetailViewModel = hiltViewModel(),
    previousBackStackEntry: NavBackStackEntry? = null,
    onNavigateToEdit: (Long) -> Unit = {},
    onNavigateToAddSongs: (Long) -> Unit = {},
    onOpenSong: (PlaybackOpenRequest) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleResumeEffect(playlistId) {
        viewModel.loadPlaylist(playlistId)
        onPauseOrDispose {}
    }

    LaunchedEffect(state) {
        if (state is PlaylistDetailUiState.Deleted) {
            previousBackStackEntry?.savedStateHandle?.set("playlist_deleted", true)
            onBack()
        }
    }

    PlaylistDetailContent(
        modifier = Modifier.fillMaxSize(),
        state = state,
        onAction = { action ->
            when (action) {
                PlaylistDetailAction.EditPlaylist -> onNavigateToEdit(playlistId)
                PlaylistDetailAction.AddSongs -> onNavigateToAddSongs(playlistId)
                PlaylistDetailAction.DeletePlaylist -> viewModel.onAction(action)
                PlaylistDetailAction.ToggleLike -> viewModel.onAction(action)
                is PlaylistDetailAction.RemoveSong -> viewModel.onAction(action)
                is PlaylistDetailAction.OpenSong -> {
                    val songs = (state as? PlaylistDetailUiState.Success)
                        ?.songs.orEmpty()
                    val queueItems = songs.toQueueItems()
                    val index = songs.indexOfFirst { it.detailLookup == action.songLookup }
                        .coerceAtLeast(0)
                    onOpenSong(
                        PlaybackOpenRequest(
                            songLookup = action.songLookup,
                            queue = queueItems,
                            startIndex = index,
                            sourceKey = "playlist:$playlistId"
                        )
                    )
                }
            }
        },
        onRetry = { viewModel.loadPlaylist(playlistId) },
        onBack = onBack,
        onDismissDeleteError = { viewModel.clearDeleteError() }
    )
}