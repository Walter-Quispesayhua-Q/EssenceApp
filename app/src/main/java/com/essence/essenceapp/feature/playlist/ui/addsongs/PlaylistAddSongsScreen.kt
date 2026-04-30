package com.essence.essenceapp.feature.playlist.ui.addsongs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.essence.essenceapp.feature.playlist.ui.addsongs.components.PlaylistAddSongsContent
import com.essence.essenceapp.ui.theme.PureWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistAddSongsScreen(
    playlistId: Long,
    viewModel: PlaylistAddSongsViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val playlistTitle by viewModel.playlistTitle.collectAsStateWithLifecycle()
    val addedDuringSession by viewModel.addedDuringSession.collectAsStateWithLifecycle()
    var query by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(playlistId) {
        viewModel.initialize(playlistId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    AddSongsTopBarTitle(
                        playlistTitle = playlistTitle,
                        addedDuringSession = addedDuringSession
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        PlaylistAddSongsContent(
            modifier = Modifier.padding(innerPadding),
            state = state,
            query = query,
            addedDuringSession = addedDuringSession,
            onAction = { action ->
                if (action is PlaylistAddSongsAction.QueryChanged) {
                    query = action.value
                }
                viewModel.onAction(action)
            },
            onDone = onBack
        )
    }
}

@Composable
private fun AddSongsTopBarTitle(
    playlistTitle: String?,
    addedDuringSession: Int
) {
    val mainText = if (!playlistTitle.isNullOrBlank()) "Agregar a $playlistTitle"
    else "Agregar canciones"

    Column {
        Text(
            text = mainText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = PureWhite,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if (addedDuringSession > 0) {
            Text(
                text = if (addedDuringSession == 1) "1 canción agregada"
                else "$addedDuringSession canciones agregadas",
                style = MaterialTheme.typography.labelSmall,
                color = PureWhite.copy(alpha = 0.55f),
                maxLines = 1
            )
        }
    }
}
