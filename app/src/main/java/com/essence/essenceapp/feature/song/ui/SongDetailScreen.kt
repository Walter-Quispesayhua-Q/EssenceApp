package com.essence.essenceapp.feature.song.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.essence.essenceapp.feature.song.ui.components.SongDetailContent
import com.essence.essenceapp.feature.song.ui.components.SongDetailTopBar

@Composable
fun SongDetailScreen(
    songId: Long,
    viewModel: SongDetailViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onOpenArtist: (Long) -> Unit = {},
    onOpenAlbum: (Long) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(songId) {
        viewModel.loadSong(songId)
    }

    Scaffold(
        topBar = {
            SongDetailTopBar(
                title = (state as? SongDetailUiState.Success)?.song?.title ?: "Canción",
                onBack = onBack,
                onRefresh = { viewModel.onAction(SongDetailAction.Refresh) }
            )
        }
    ) { innerPadding ->
        SongDetailContent(
            modifier = Modifier.padding(innerPadding),
            state = state,
            onAction = { action ->
                when (action) {
                    SongDetailAction.Back -> onBack()
                    SongDetailAction.Refresh -> viewModel.onAction(action)
                    is SongDetailAction.OpenArtist -> onOpenArtist(action.artistId)
                    is SongDetailAction.OpenAlbum -> onOpenAlbum(action.albumId)
                }
            },
            onManagerAction = viewModel::onManagerAction
        )
    }
}
