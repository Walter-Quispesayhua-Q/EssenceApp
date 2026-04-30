package com.essence.essenceapp.feature.history.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.essence.essenceapp.feature.history.ui.components.HistoryContent
import com.essence.essenceapp.feature.history.ui.components.HistoryTopBar
import com.essence.essenceapp.shared.playback.model.PlaybackOpenRequest

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel(),
    onBack: (() -> Unit)? = null,
    onOpenSong: (PlaybackOpenRequest) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleResumeEffect(Unit) {
        viewModel.silentRefresh()
        onPauseOrDispose {}
    }

    val subtitle = when (val s = state) {
        is HistoryUiState.Success -> if (s.songs.isEmpty()) null else "${s.songs.size} canciones"
        else -> null
    }

    Scaffold(
        topBar = {
            HistoryTopBar(
                onBack = onBack,
                subtitle = subtitle
            )
        }
    ) { innerPadding ->
        HistoryContent(
            modifier = Modifier.padding(innerPadding),
            state = state,
            onRetry = { viewModel.onAction(HistoryAction.Refresh) },
            onOpenSong = onOpenSong
        )
    }
}