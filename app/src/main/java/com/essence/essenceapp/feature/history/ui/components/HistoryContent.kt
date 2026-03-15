package com.essence.essenceapp.feature.history.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.history.ui.HistoryUiState
import com.essence.essenceapp.feature.song.domain.model.SongSimple
import com.essence.essenceapp.shared.ui.components.cards.BaseCard
import com.essence.essenceapp.shared.ui.components.cards.song.CompactSongContent
import com.essence.essenceapp.shared.ui.components.status.error.AppErrorState
import com.essence.essenceapp.ui.shell.LocalBottomBarClearance
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import java.time.LocalDate

@Composable
fun HistoryContent(
    modifier: Modifier = Modifier,
    state: HistoryUiState,
    onRetry: () -> Unit,
    onOpenSong: (Long) -> Unit
) {
    when (state) {
        HistoryUiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(28.dp))
            }
        }

        is HistoryUiState.Error -> {
            AppErrorState(
                modifier = modifier,
                title = "No se pudo cargar el historial",
                message = state.message,
                onRetry = onRetry
            )
        }

        is HistoryUiState.Success -> {
            val bottomClearance = LocalBottomBarClearance.current

            LazyColumn(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 12.dp,
                    bottom = bottomClearance + 16.dp
                )
            ) {
                item {
                    BaseCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Tus canciones reproducidas recientemente",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                if (state.songs.isEmpty()) {
                    item {
                        BaseCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Tu historial está vacío por ahora.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                            )
                        }
                    }
                } else {
                    items(state.songs, key = { it.id }) { song ->
                        BaseCard(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onOpenSong(song.id) }
                        ) {
                            CompactSongContent(
                                song = song,
                                durationText = formatDuration(song.durationMs.toLong())
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

private val previewSongs = listOf(
    SongSimple(
        id = 1L,
        title = "Blinding Lights",
        durationMs = 200_000,
        hlsMasterKey = "",
        imageKey = null,
        songType = "single",
        totalPlays = 1_000_000L,
        artistName = "The Weeknd",
        albumName = "After Hours",
        releaseDate = LocalDate.of(2020, 3, 20)
    ),
    SongSimple(
        id = 2L,
        title = "Starboy",
        durationMs = 230_000,
        hlsMasterKey = "",
        imageKey = null,
        songType = "single",
        totalPlays = 900_000L,
        artistName = "The Weeknd",
        albumName = "Starboy",
        releaseDate = LocalDate.of(2016, 11, 25)
    )
)

@Preview(name = "History - Loading", showBackground = true)
@Composable
private fun HistoryContentLoadingPreview() {
    EssenceAppTheme {
        CompositionLocalProvider(LocalBottomBarClearance provides 92.dp) {
            HistoryContent(
                state = HistoryUiState.Loading,
                onRetry = {},
                onOpenSong = {}
            )
        }
    }
}

@Preview(name = "History - Error", showBackground = true)
@Composable
private fun HistoryContentErrorPreview() {
    EssenceAppTheme {
        CompositionLocalProvider(LocalBottomBarClearance provides 92.dp) {
            HistoryContent(
                state = HistoryUiState.Error("No se pudo obtener el historial."),
                onRetry = {},
                onOpenSong = {}
            )
        }
    }
}

@Preview(name = "History - Empty", showBackground = true)
@Composable
private fun HistoryContentEmptyPreview() {
    EssenceAppTheme {
        CompositionLocalProvider(LocalBottomBarClearance provides 92.dp) {
            HistoryContent(
                state = HistoryUiState.Success(songs = emptyList()),
                onRetry = {},
                onOpenSong = {}
            )
        }
    }
}

@Preview(name = "History - Success", showBackground = true)
@Composable
private fun HistoryContentSuccessPreview() {
    EssenceAppTheme {
        CompositionLocalProvider(LocalBottomBarClearance provides 92.dp) {
            HistoryContent(
                state = HistoryUiState.Success(songs = previewSongs),
                onRetry = {},
                onOpenSong = {}
            )
        }
    }
}