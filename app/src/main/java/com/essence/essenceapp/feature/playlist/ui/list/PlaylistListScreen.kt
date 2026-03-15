package com.essence.essenceapp.feature.playlist.ui.list

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.essence.essenceapp.feature.playlist.ui.list.componets.PlaylistListContent
import com.essence.essenceapp.feature.playlist.ui.list.componets.PlaylistListTopBar

@Composable
fun PlaylistListScreen(
    viewModel: PlaylistListViewModel = hiltViewModel(),
    onNavigateToDetail: (Long) -> Unit = {},
    onNavigateToCreate: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            PlaylistListTopBar(
                title = "Mi Biblioteca",
                onCreatePlaylist = onNavigateToCreate
            )
        }
    ) { innerPadding ->
        PlaylistListContent(
            modifier = Modifier.padding(innerPadding),
            state = state,
            onAction = { action ->
                when (action) {
                    is PlaylistListAction.OpenDetail -> onNavigateToDetail(action.id)
                    PlaylistListAction.CreatePlaylist -> onNavigateToCreate()
                    PlaylistListAction.OpenHistory -> onNavigateToHistory()
                    else -> viewModel.onAction(action)
                }
            }
        )
    }
}