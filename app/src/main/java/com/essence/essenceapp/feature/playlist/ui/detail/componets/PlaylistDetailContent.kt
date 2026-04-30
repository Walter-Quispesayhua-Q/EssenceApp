package com.essence.essenceapp.feature.playlist.ui.detail.componets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.essence.essenceapp.feature.playlist.domain.model.Playlist
import com.essence.essenceapp.feature.playlist.ui.detail.PlaylistDetailAction
import com.essence.essenceapp.feature.playlist.ui.detail.PlaylistDetailUiState
import com.essence.essenceapp.feature.song.domain.model.SongSimple
import com.essence.essenceapp.shared.ui.components.status.error.AppErrorState
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import java.time.LocalDate

@Composable
fun PlaylistDetailContent(
    modifier: Modifier = Modifier,
    state: PlaylistDetailUiState,
    onAction: (PlaylistDetailAction) -> Unit,
    onRetry: () -> Unit = {},
    onBack: () -> Unit = {},
    onDismissDeleteError: () -> Unit = {}
) {
    when (state) {
        PlaylistDetailUiState.Loading -> PlaylistDetailLoading(modifier = modifier)

        PlaylistDetailUiState.Deleted -> PlaylistDetailLoading(modifier = modifier)

        is PlaylistDetailUiState.Error -> AppErrorState(
            modifier = modifier,
            message = state.message,
            title = "No se pudo cargar la playlist",
            onRetry = onRetry
        )

        is PlaylistDetailUiState.Success -> PlaylistDetailSuccess(
            modifier = modifier,
            playlist = state.playlist,
            songs = state.songs,
            isSongsLoading = state.isSongsLoading,
            isRefreshing = state.isRefreshing,
            isLikeSubmitting = state.isLikeSubmitting,
            isDeleting = state.isDeleting,
            deleteError = state.deleteError,
            onAction = onAction,
            onBack = onBack,
            onDismissDeleteError = onDismissDeleteError
        )
    }
}

// Previews

private val previewPlaylist = Playlist(
    id = 1L,
    title = "Noches Lo-Fi",
    description = "Selección para estudiar y concentrarse con calma.",
    imageKey = null,
    isPublic = true,
    totalSongs = 3,
    createdAt = LocalDate.of(2025, 11, 20),
    updatedAt = LocalDate.of(2026, 3, 2),
    totalLikes = 42,
    isLiked = true
)

private val previewLikedPlaylist = Playlist(
    id = 99L,
    title = "Liked Songs",
    description = null,
    imageKey = null,
    isPublic = false,
    totalSongs = 5,
    createdAt = LocalDate.of(2025, 1, 1),
    updatedAt = LocalDate.of(2026, 3, 30),
    type = "LIKED"
)

private val previewSongs = listOf(
    SongSimple(
        id = 1L,
        title = "Tití Me Preguntó",
        durationMs = 210_000,
        hlsMasterKey = "songs/titi/master.m3u8",
        imageKey = null,
        songType = "single",
        totalPlays = 1_200_000L,
        artistName = "Bad Bunny",
        albumName = "Un Verano Sin Ti",
        releaseDate = LocalDate.of(2022, 5, 6)
    ),
    SongSimple(
        id = 2L,
        title = "Ojitos Lindos",
        durationMs = 245_000,
        hlsMasterKey = "songs/ojitos/master.m3u8",
        imageKey = null,
        songType = "single",
        totalPlays = 900_000L,
        artistName = "Bad Bunny",
        albumName = "Un Verano Sin Ti",
        releaseDate = LocalDate.of(2022, 5, 6)
    ),
    SongSimple(
        id = 3L,
        title = "Efecto",
        durationMs = 198_000,
        hlsMasterKey = "songs/efecto/master.m3u8",
        imageKey = null,
        songType = "single",
        totalPlays = 750_000L,
        artistName = "Bad Bunny",
        albumName = "Un Verano Sin Ti",
        releaseDate = LocalDate.of(2022, 5, 6)
    )
)

@Preview(name = "Playlist Detail - Success", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun SuccessPreview() {
    EssenceAppTheme {
        PlaylistDetailContent(
            state = PlaylistDetailUiState.Success(
                playlist = previewPlaylist,
                songs = previewSongs,
                isSongsLoading = false,
                isLikeSubmitting = false
            ),
            onAction = {},
            onBack = {}
        )
    }
}

@Preview(name = "Playlist Detail - LIKED", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun LikedPreview() {
    EssenceAppTheme {
        PlaylistDetailContent(
            state = PlaylistDetailUiState.Success(
                playlist = previewLikedPlaylist,
                songs = previewSongs,
                isSongsLoading = false,
                isLikeSubmitting = false
            ),
            onAction = {},
            onBack = {}
        )
    }
}

@Preview(name = "Playlist Detail - LIKED Empty", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun LikedEmptyPreview() {
    EssenceAppTheme {
        PlaylistDetailContent(
            state = PlaylistDetailUiState.Success(
                playlist = previewLikedPlaylist.copy(totalSongs = 0),
                songs = emptyList(),
                isSongsLoading = false,
                isLikeSubmitting = false
            ),
            onAction = {},
            onBack = {}
        )
    }
}

@Preview(name = "Playlist Detail - Empty Private", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun EmptyPreview() {
    EssenceAppTheme {
        PlaylistDetailContent(
            state = PlaylistDetailUiState.Success(
                playlist = previewPlaylist.copy(totalSongs = 0, isPublic = false),
                songs = emptyList(),
                isSongsLoading = false,
                isLikeSubmitting = false
            ),
            onAction = {},
            onBack = {}
        )
    }
}

@Preview(name = "Playlist Detail - Loading", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun LoadingPreview() {
    EssenceAppTheme {
        PlaylistDetailContent(
            state = PlaylistDetailUiState.Loading,
            onAction = {},
            onBack = {}
        )
    }
}
