package com.essence.essenceapp.feature.album.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.essence.essenceapp.feature.album.ui.components.AlbumDetailContent
import com.essence.essenceapp.shared.playback.mapper.toQueueItems
import com.essence.essenceapp.shared.playback.model.PlaybackOpenRequest

@Composable
fun AlbumDetailScreen(
    albumLookup: String,
    viewModel: AlbumDetailViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onOpenSong: (PlaybackOpenRequest) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(albumLookup) {
        viewModel.loadAlbum(albumLookup)
    }

    AlbumDetailContent(
        state = state,
        onAction = { action ->
            when (action) {
                AlbumDetailAction.Back -> onBack()
                AlbumDetailAction.Refresh -> viewModel.onAction(action)
                AlbumDetailAction.ToggleLike -> viewModel.onAction(action)
                is AlbumDetailAction.OpenSong -> {
                    val songs = (state as? AlbumDetailUiState.Success)
                        ?.album?.songs.orEmpty()
                    val queueItems = songs.toQueueItems()
                    val index = songs.indexOfFirst { it.detailLookup == action.songLookup }
                        .coerceAtLeast(0)
                    onOpenSong(
                        PlaybackOpenRequest(
                            songLookup = action.songLookup,
                            queue = queueItems,
                            startIndex = index,
                            sourceKey = "album:$albumLookup"
                        )
                    )
                }
            }
        }
    )
}