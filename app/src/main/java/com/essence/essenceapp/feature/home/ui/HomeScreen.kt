package com.essence.essenceapp.feature.home.ui

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.essence.essenceapp.feature.home.ui.components.HomeContent
import com.essence.essenceapp.feature.home.ui.components.HomeTopBar

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    isLoggedIn: Boolean = false,
    username: String? = null,
    onLoginClick: () -> Unit = {},
    onOpenSong: (String) -> Unit = {},
    onOpenAlbum: (String) -> Unit = {},
    onOpenArtist: (String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        Log.e("HOME_DEBUG", "HomeScreen ENTER")
    }

    Scaffold(
        topBar = {
            HomeTopBar(
                username = username,
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