package com.essence.essenceapp.feature.song.ui.playback

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.essence.essenceapp.feature.song.ui.playback.components.PlaybackManagerContent
import com.essence.essenceapp.ui.theme.MidnightBlack

@Composable
fun PlaybackManagerScreen(
    modifier: Modifier = Modifier,
    state: PlaybackUiState,
    onAction: (PlaybackAction) -> Unit,
    songTitle: String? = null,
    artistName: String? = null,
    isLiked: Boolean = false,
    isLikeSubmitting: Boolean = false,
    onToggleLike: (() -> Unit)? = null
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MidnightBlack
    ) { innerPadding ->
        PlaybackManagerContent(
            modifier = Modifier.padding(innerPadding),
            state = state,
            onAction = onAction,
            songTitle = songTitle,
            artistName = artistName,
            isLiked = isLiked,
            isLikeSubmitting = isLikeSubmitting,
            onToggleLike = onToggleLike
        )
    }
}