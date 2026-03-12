package com.essence.essenceapp.feature.album.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.essence.essenceapp.feature.album.ui.components.AlbumDetailContent
import com.essence.essenceapp.feature.album.ui.components.AlbumDetailTopBar

@Composable
fun AlbumDetailScreen(
    albumId: Long,
    viewModel: AlbumDetailViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onOpenSong: (Long) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(albumId) {
        viewModel.loadAlbum(albumId)
    }

    Scaffold(
        topBar = {
            AlbumDetailTopBar(
                title = (state as? AlbumDetailUiState.Success)?.album?.title ?: "Álbum",
                onBack = onBack,
                onRefresh = { viewModel.onAction(AlbumDetailAction.Refresh) }
            )
        }
    ) { innerPadding ->
        AlbumDetailContent(
            modifier = Modifier.padding(innerPadding),
            state = state,
            onAction = { action ->
                when (action) {
                    AlbumDetailAction.Back -> onBack()
                    AlbumDetailAction.Refresh -> viewModel.onAction(action)
                    is AlbumDetailAction.OpenSong -> onOpenSong(action.songId)
                }
            }
        )
    }
}
