package com.essence.essenceapp.feature.playlist.ui.form

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.essence.essenceapp.feature.playlist.ui.form.componets.PlaylistFormContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistFormSheet(
    playlistId: Long? = null,
    viewModel: PlaylistFormViewModel = hiltViewModel(),
    onDismiss: () -> Unit = {},
    onSuccess: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(playlistId) {
        viewModel.initialize(playlistId)
    }

    LaunchedEffect(state) {
        if (state is PlaylistFormUiState.Success) onSuccess()
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        PlaylistFormContent(
            state = state,
            isEditing = playlistId != null,
            onAction = viewModel::onAction,
            onDismiss = onDismiss,
            modifier = Modifier.padding(16.dp)
        )
    }
}
