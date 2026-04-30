package com.essence.essenceapp.feature.artist.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.essence.essenceapp.feature.artist.ui.components.ArtistDetailContent
import com.essence.essenceapp.shared.playback.mapper.toQueueItems
import com.essence.essenceapp.shared.playback.model.PlaybackOpenRequest

@Composable
fun ArtistDetailScreen(
    artistLookup: String,
    viewModel: ArtistDetailViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onOpenSong: (PlaybackOpenRequest) -> Unit = {},
    onOpenAlbum: (String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(artistLookup) {
        viewModel.loadArtist(artistLookup)
    }

    ArtistDetailContent(
        state = state,
        onAction = { action ->
            when (action) {
                ArtistDetailAction.Back -> onBack()
                ArtistDetailAction.Refresh -> viewModel.onAction(action)
                ArtistDetailAction.ToggleLike -> viewModel.onAction(action)
                is ArtistDetailAction.OpenSong -> {
                    val songs = (state as? ArtistDetailUiState.Success)
                        ?.artist?.songs.orEmpty()
                    val queueItems = songs.toQueueItems()
                    val index = songs.indexOfFirst { it.detailLookup == action.songLookup }
                        .coerceAtLeast(0)
                    onOpenSong(
                        PlaybackOpenRequest(
                            songLookup = action.songLookup,
                            queue = queueItems,
                            startIndex = index,
                            sourceKey = "artist:$artistLookup"
                        )
                    )
                }
                is ArtistDetailAction.OpenAlbum -> onOpenAlbum(action.albumLookup)
            }
        }
    )
}