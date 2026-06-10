package com.essence.essenceapp.feature.album.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.essence.essenceapp.feature.album.ui.components.AlbumDetailContent
import com.essence.essenceapp.feature.playback.domain.PlaybackOpenRequest
import com.essence.essenceapp.feature.playback.domain.PlaybackSource
import com.essence.essenceapp.feature.playback.mapper.toQueueItems

@Composable
fun AlbumDetailScreen(
    albumLookup: String,
    viewModel: AlbumDetailViewModel = hiltViewModel(),
    isLoggedIn: Boolean = false,
    onBack: () -> Unit = {},
    onOpenSong: (PlaybackOpenRequest) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(albumLookup) {
        viewModel.loadAlbum(albumLookup)
    }

    AlbumDetailContent(
        state = state,
        isLoggedIn = isLoggedIn,
        onAction = { action ->
            when (action) {
                AlbumDetailAction.Back -> onBack()
                AlbumDetailAction.Refresh -> viewModel.onAction(action)
                AlbumDetailAction.ToggleLike -> viewModel.onAction(action)
                is AlbumDetailAction.OpenSong -> {
                    val songs = (state as? AlbumDetailUiState.Success)
                        ?.album?.songs.orEmpty()
                    val queueItems = songs.toQueueItems()
                    val index = songs.indexOfFirst { it.detailLookup == action.hlsMasterKey }
                        .coerceAtLeast(0)
                    onOpenSong(
                        PlaybackOpenRequest(
                            items = queueItems,
                            startIndex = index,
                            source = PlaybackSource(
                                type = PlaybackSource.SourceType.ALBUM,
                                sourceId = albumLookup
                            )
                        )
                    )
                }
            }
        }
    )
}