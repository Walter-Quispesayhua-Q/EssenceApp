package com.essence.essenceapp.feature.song.ui.manager

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.essence.essenceapp.feature.song.ui.manager.components.PlaybackManagerContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaybackManagerScreen(
    state: PlaybackUiState,
    onAction: (SongDetailManagerAction) -> Unit,
    onBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reproductor") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        PlaybackManagerContent(
            modifier = Modifier.padding(innerPadding),
            state = state,
            onAction = onAction
        )
    }
}
