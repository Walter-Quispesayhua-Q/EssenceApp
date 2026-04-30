package com.essence.essenceapp.feature.song.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.essence.essenceapp.feature.song.ui.components.SongDetailContent
import com.essence.essenceapp.shared.ui.components.playlist.AddToPlaylistSheet

@Composable
fun SongDetailScreen(
    songLookup: String,
    viewModel: SongDetailViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onOpenArtist: (String) -> Unit = {},
    onOpenAlbum: (String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var playlistSheetSongKey by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(songLookup) {
        viewModel.loadSong(songLookup)
    }

    SongDetailContent(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
            },
        state = state,
        onAction = { action ->
            when (action) {
                SongDetailAction.Back -> onBack()
                SongDetailAction.Refresh -> viewModel.onAction(action)
                SongDetailAction.ToggleLike -> viewModel.onAction(action)
                SongDetailAction.AddToPlaylist -> {
                    val currentSong = (state as? SongDetailUiState.Success)?.song
                    if (currentSong != null) {
                        playlistSheetSongKey = currentSong.hlsMasterKey
                    }
                }
                is SongDetailAction.OpenArtist -> onOpenArtist(action.artistLookup)
                is SongDetailAction.OpenAlbum -> onOpenAlbum(action.albumLookup)
                is SongDetailAction.PlayQueueItem -> viewModel.onAction(action)
            }
        },
        onPlaybackAction = viewModel::onPlaybackAction
    )

    playlistSheetSongKey?.let { key ->
        AddToPlaylistSheet(
            songKey = key,
            onDismiss = { playlistSheetSongKey = null }
        )
    }
}