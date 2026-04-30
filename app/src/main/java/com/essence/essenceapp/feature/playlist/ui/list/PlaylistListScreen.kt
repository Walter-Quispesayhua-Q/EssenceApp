package com.essence.essenceapp.feature.playlist.ui.list

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import com.essence.essenceapp.feature.playlist.ui.list.componets.PlaylistListContent
import com.essence.essenceapp.feature.playlist.ui.list.componets.PlaylistListTopBar

@Composable
fun PlaylistListScreen(
    viewModel: PlaylistListViewModel = hiltViewModel(),
    navBackStackEntry: NavBackStackEntry? = null,
    onNavigateToDetail: (Long) -> Unit = {},
    onNavigateToCreate: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LifecycleResumeEffect(Unit) {
        viewModel.silentRefresh()
        onPauseOrDispose {}
    }

    navBackStackEntry?.savedStateHandle?.let { handle ->
        val updated = handle.get<Boolean>("playlist_updated") == true
        LaunchedEffect(updated) {
            if (updated) {
                handle.remove<Boolean>("playlist_updated")
                viewModel.onAction(PlaylistListAction.Refresh)
            }
        }

        val deleted = handle.get<Boolean>("playlist_deleted") == true
        LaunchedEffect(deleted) {
            if (deleted) {
                handle.remove<Boolean>("playlist_deleted")
                viewModel.silentRefresh()
                snackbarHostState.showSnackbar(
                    message = "Playlist eliminada",
                    withDismissAction = true
                )
            }
        }
    }

    val subtitle = (state as? PlaylistListUiState.Success)?.let { success ->
        val total = success.playlist.myPlaylists.orEmpty().size +
                success.playlist.playlistsPublic.orEmpty().size
        if (total == 0) null else "$total playlists"
    }

    Scaffold(
        topBar = {
            PlaylistListTopBar(
                title = "Mi Biblioteca",
                subtitle = subtitle,
                onCreatePlaylist = onNavigateToCreate
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
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