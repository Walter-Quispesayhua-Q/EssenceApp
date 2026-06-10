package com.essence.essenceapp.feature.artist.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.essence.essenceapp.feature.artist.ui.components.ArtistDetailContent
import com.essence.essenceapp.feature.playback.domain.PlaybackOpenRequest
import com.essence.essenceapp.feature.playback.domain.PlaybackSource
import com.essence.essenceapp.feature.playback.mapper.toQueueItems

@Composable
fun ArtistDetailScreen(
    artistLookup: String,
    viewModel: ArtistDetailViewModel = hiltViewModel(),
    isLoggedIn: Boolean = false,
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
        isLoggedIn = isLoggedIn,
        onAction = { action ->
            when (action) {
                ArtistDetailAction.Back -> onBack()
                ArtistDetailAction.Refresh -> viewModel.onAction(action)
                ArtistDetailAction.ToggleLike -> viewModel.onAction(action)
                is ArtistDetailAction.OpenSong -> {
                    val songs = (state as? ArtistDetailUiState.Success)
                        ?.artist?.songs.orEmpty()
                    val queueItems = songs.toQueueItems()
                    val index = songs.indexOfFirst { it.detailLookup == action.hlsMasterKey }
                        .coerceAtLeast(0)
                    onOpenSong(
                        PlaybackOpenRequest(
                            items = queueItems,
                            startIndex = index,
                            source = PlaybackSource(
                                type = PlaybackSource.SourceType.ARTIST,
                                sourceId = artistLookup
                            )
                        )
                    )
                }
                is ArtistDetailAction.OpenAlbum -> onOpenAlbum(action.albumLookup)
            }
        }
    )
}