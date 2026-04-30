package com.essence.essenceapp.feature.album.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.essence.essenceapp.feature.album.ui.AlbumDetailAction
import com.essence.essenceapp.feature.album.ui.AlbumDetailUiState
import com.essence.essenceapp.shared.ui.components.status.error.AppErrorState
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.MidnightBlack

@Composable
fun AlbumDetailContent(
    modifier: Modifier = Modifier,
    state: AlbumDetailUiState,
    onAction: (AlbumDetailAction) -> Unit
) {
    when (state) {
        AlbumDetailUiState.Loading -> AlbumDetailLoadingState(modifier = modifier)

        is AlbumDetailUiState.Error -> AppErrorState(
            modifier = modifier
                .fillMaxSize()
                .background(MidnightBlack),
            message = state.message,
            title = "No se pudo cargar el album",
            onRetry = { onAction(AlbumDetailAction.Refresh) }
        )

        is AlbumDetailUiState.Success -> AlbumDetailSuccess(
            modifier = modifier,
            album = state.album,
            isLikeSubmitting = state.isLikeSubmitting,
            onAction = onAction
        )
    }
}

@Preview(name = "Album - Loading", showBackground = true, backgroundColor = 0xFF0E0E10)
@Composable
private fun AlbumDetailLoadingPreview() {
    EssenceAppTheme {
        AlbumDetailContent(state = AlbumDetailUiState.Loading, onAction = {})
    }
}

@Preview(name = "Album - Success", showBackground = true, backgroundColor = 0xFF0E0E10)
@Composable
private fun AlbumDetailSuccessPreview() {
    EssenceAppTheme {
        AlbumDetailContent(
            state = AlbumDetailUiState.Success(
                album = previewAlbumSample,
                isLikeSubmitting = false
            ),
            onAction = {}
        )
    }
}

@Preview(name = "Album - Empty tracks", showBackground = true, backgroundColor = 0xFF0E0E10)
@Composable
private fun AlbumDetailEmptyPreview() {
    EssenceAppTheme {
        AlbumDetailContent(
            state = AlbumDetailUiState.Success(
                album = previewAlbumSample.copy(songs = emptyList()),
                isLikeSubmitting = false
            ),
            onAction = {}
        )
    }
}

@Preview(name = "Album - Error", showBackground = true, backgroundColor = 0xFF0E0E10)
@Composable
private fun AlbumDetailErrorPreview() {
    EssenceAppTheme {
        AlbumDetailContent(
            state = AlbumDetailUiState.Error(message = "Sin conexion a internet"),
            onAction = {}
        )
    }
}
