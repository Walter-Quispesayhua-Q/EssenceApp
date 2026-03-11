package com.essence.essenceapp.feature.playlist.ui.detail

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.essence.essenceapp.feature.playlist.ui.detail.componets.PlaylistDetailContent
import com.essence.essenceapp.feature.playlist.ui.detail.componets.PlaylistDetailTopBar

@Composable
fun PlaylistDetailScreen(
    playlistId: Long,
    viewModel: PlaylistDetailViewModel = hiltViewModel(),
    onNavigateToEdit: (Long) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(playlistId) {
        viewModel.loadPlaylist(playlistId)
    }

    Scaffold(
        topBar = {
            PlaylistDetailTopBar(
                title = (state as? PlaylistDetailUiState.Success)?.playlist?.title ?: "Playlist",
                onBack = onBack,
                onEdit = { onNavigateToEdit(playlistId) }
            )
        }
    ) { innerPadding ->
        PlaylistDetailContent(
            modifier = Modifier.padding(innerPadding),
            state = state,
            onAction = viewModel::onAction
        )
    }
}
