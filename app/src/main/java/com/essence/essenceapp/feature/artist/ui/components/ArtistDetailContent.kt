package com.essence.essenceapp.feature.artist.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.essence.essenceapp.feature.artist.ui.ArtistDetailAction
import com.essence.essenceapp.feature.artist.ui.ArtistDetailUiState
import com.essence.essenceapp.shared.ui.components.status.error.AppErrorState
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.MidnightBlack

@Composable
fun ArtistDetailContent(
    modifier: Modifier = Modifier,
    state: ArtistDetailUiState,
    onAction: (ArtistDetailAction) -> Unit
) {
    when (state) {
        ArtistDetailUiState.Loading -> ArtistDetailLoadingState(modifier = modifier)

        is ArtistDetailUiState.Error -> AppErrorState(
            modifier = modifier
                .fillMaxSize()
                .background(MidnightBlack),
            message = state.message,
            title = "No se pudo cargar el artista",
            onRetry = { onAction(ArtistDetailAction.Refresh) }
        )

        is ArtistDetailUiState.Success -> ArtistDetailSuccess(
            modifier = modifier,
            artist = state.artist,
            isLikeSubmitting = state.isLikeSubmitting,
            onAction = onAction
        )
    }
}

@Preview(name = "Artist - Loading", showBackground = true, backgroundColor = 0xFF0E0E10)
@Composable
private fun ArtistDetailLoadingPreview() {
    EssenceAppTheme {
        ArtistDetailContent(state = ArtistDetailUiState.Loading, onAction = {})
    }
}

@Preview(name = "Artist - Success", showBackground = true, backgroundColor = 0xFF0E0E10)
@Composable
private fun ArtistDetailSuccessPreview() {
    EssenceAppTheme {
        ArtistDetailContent(
            state = ArtistDetailUiState.Success(
                artist = previewArtistSample,
                isLikeSubmitting = false
            ),
            onAction = {}
        )
    }
}

@Preview(name = "Artist - Empty content", showBackground = true, backgroundColor = 0xFF0E0E10)
@Composable
private fun ArtistDetailEmptyPreview() {
    EssenceAppTheme {
        ArtistDetailContent(
            state = ArtistDetailUiState.Success(
                artist = previewArtistSample.copy(songs = emptyList(), albums = emptyList()),
                isLikeSubmitting = false
            ),
            onAction = {}
        )
    }
}

@Preview(name = "Artist - Error", showBackground = true, backgroundColor = 0xFF0E0E10)
@Composable
private fun ArtistDetailErrorPreview() {
    EssenceAppTheme {
        ArtistDetailContent(
            state = ArtistDetailUiState.Error(message = "Sin conexion a internet"),
            onAction = {}
        )
    }
}
