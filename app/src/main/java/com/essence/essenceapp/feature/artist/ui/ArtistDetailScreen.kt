package com.essence.essenceapp.feature.artist.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.essence.essenceapp.feature.artist.ui.components.ArtistDetailContent
import com.essence.essenceapp.feature.artist.ui.components.ArtistDetailTopBar

@Composable
fun ArtistDetailScreen(
    artistLookup: String,
    viewModel: ArtistDetailViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onOpenSong: (String) -> Unit = {},
    onOpenAlbum: (String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(artistLookup) {
        viewModel.loadArtist(artistLookup)
    }

    Scaffold(
        topBar = {
            ArtistDetailTopBar(
                title = (state as? ArtistDetailUiState.Success)?.artist?.nameArtist ?: "Artista",
                onBack = onBack
            )
        }
    ) { innerPadding ->
        ArtistDetailContent(
            modifier = Modifier.padding(innerPadding),
            state = state,
            onAction = { action ->
                when (action) {
                    ArtistDetailAction.Back -> onBack()
                    ArtistDetailAction.Refresh -> viewModel.onAction(action)
                    ArtistDetailAction.ToggleLike -> viewModel.onAction(action)
                    is ArtistDetailAction.OpenSong -> onOpenSong(action.songLookup)
                    is ArtistDetailAction.OpenAlbum -> onOpenAlbum(action.albumLookup)
                }
            }
        )
    }
}