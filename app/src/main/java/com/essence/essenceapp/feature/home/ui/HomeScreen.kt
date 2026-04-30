package com.essence.essenceapp.feature.home.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.essence.essenceapp.feature.home.ui.components.HomeContent
import com.essence.essenceapp.feature.home.ui.components.HomeTopBar
import com.essence.essenceapp.shared.playback.model.PlaybackOpenRequest

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    isLoggedIn: Boolean = false,
    username: String? = null,
    onLoginClick: () -> Unit = {},
    onOpenSong: (PlaybackOpenRequest) -> Unit = {},
    onOpenAlbum: (String) -> Unit = {},
    onOpenArtist: (String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val currentUsername by viewModel.username.collectAsStateWithLifecycle()

    val displayUsername = currentUsername ?: username

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            viewModel.refreshCurrentUser()
        } else {
            viewModel.clearCurrentUser()
        }
    }

    LifecycleResumeEffect(Unit) {
        viewModel.refreshIfStale()
        if (isLoggedIn) {
            viewModel.refreshCurrentUser()
            viewModel.refreshHistory()
        }
        onPauseOrDispose {}
    }

    Scaffold(
        topBar = {
            HomeTopBar(
                username = displayUsername,
                isLoggedIn = isLoggedIn,
                onLoginClick = onLoginClick
            )
        }
    ) { innerPadding ->
        HomeContent(
            modifier = Modifier.padding(innerPadding),
            state = state,
            isLoggedIn = isLoggedIn,
            onRefresh = { viewModel.onRefresh() },
            onOpenSong = onOpenSong,
            onOpenAlbum = onOpenAlbum,
            onOpenArtist = onOpenArtist
        )
    }
}