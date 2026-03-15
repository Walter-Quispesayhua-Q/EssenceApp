package com.essence.essenceapp.feature.history.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.essence.essenceapp.feature.history.ui.components.HistoryContent
import com.essence.essenceapp.feature.history.ui.components.HistoryTopBar

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel(),
    onBack: (() -> Unit)? = null,
    onOpenSong: (Long) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            HistoryTopBar(onBack = onBack)
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