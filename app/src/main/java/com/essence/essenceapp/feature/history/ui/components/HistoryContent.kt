package com.essence.essenceapp.feature.history.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.history.ui.HistoryUiState
import com.essence.essenceapp.feature.song.domain.model.SongSimple
import com.essence.essenceapp.shared.ui.components.cards.song.CompactSongContent
import com.essence.essenceapp.shared.ui.components.status.error.AppErrorState
import com.essence.essenceapp.ui.shell.LocalBottomBarClearance
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.SoftRose
import java.time.LocalDate

@Composable
fun HistoryContent(
    modifier: Modifier = Modifier,
    state: HistoryUiState,
    onRetry: () -> Unit,
    onOpenSong: (String) -> Unit
) {
    when (state) {
        HistoryUiState.Loading -> HistoryLoadingState(modifier)
        is HistoryUiState.Error -> AppErrorState(
            modifier = modifier,
            title = "No se pudo cargar el historial",
            message = state.message,
            onRetry = onRetry
        )
        is HistoryUiState.Success -> {
            if (state.songs.isEmpty()) {
                HistoryEmptyState(modifier)
            } else {
                HistorySuccessState(
                    modifier = modifier,
                    songs = state.songs,
                    onOpenSong = onOpenSong
                )
            }
        }
    }
}

@Composable
private fun HistorySuccessState(
    modifier: Modifier = Modifier,
    songs: List<SongSimple>,
    onOpenSong: (String) -> Unit
) {
    val bottomClearance = LocalBottomBarClearance.current

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 16.dp,
            bottom = bottomClearance + 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionHeader(
                title = "Reproducidas recientemente",
                count = songs.size,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item {
            SongsIsland(
                songs = songs,
                onOpenSong = onOpenSong,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(18.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(MutedTeal, MutedTeal.copy(alpha = 0.3f))
                    ),
                    shape = RoundedCornerShape(2.dp)
                )
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "$count",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
        )
    }
}

@Composable
private fun SongsIsland(
    songs: List<SongSimple>,
    onOpenSong: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            songs.forEachIndexed { index, song ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenSong(song.detailLookup) }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    CompactSongContent(
                        song = song,
                        durationText = formatDuration(song.durationMs.toLong())
                    )
                }
                if (index < songs.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 14.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryLoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.5.dp,
                color = MutedTeal
            )
            Text(
                text = "Cargando historial...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun HistoryEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(64.dp),
            shape = CircleShape,
            color = MutedTeal.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.History,
                    contentDescription = null,
                    tint = MutedTeal,
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Sin historial aún",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Las canciones que escuches aparecerán aquí",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )
    }
}

private fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

private val previewSongs = listOf(
    SongSimple(1L, "Blinding Lights", 200_000, "", null, "single", 1_000_000L, "The Weeknd", "After Hours", LocalDate.of(2020, 3, 20)),
    SongSimple(2L, "Starboy", 230_000, "", null, "single", 900_000L, "The Weeknd", "Starboy", LocalDate.of(2016, 11, 25)),
    SongSimple(3L, "Save Your Tears", 215_000, "", null, "single", 850_000L, "The Weeknd", "After Hours", LocalDate.of(2020, 3, 20))
)

@Preview(name = "History - Loading", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun LoadingPreview() {
    EssenceAppTheme {
        CompositionLocalProvider(LocalBottomBarClearance provides 92.dp) {
            HistoryContent(state = HistoryUiState.Loading, onRetry = {}, onOpenSong = {})
        }
    }
}

@Preview(name = "History - Empty", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun EmptyPreview() {
    EssenceAppTheme {
        CompositionLocalProvider(LocalBottomBarClearance provides 92.dp) {
            HistoryContent(state = HistoryUiState.Success(songs = emptyList()), onRetry = {}, onOpenSong = {})
        }
    }
}

@Preview(name = "History - Success", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun SuccessPreview() {
    EssenceAppTheme {
        CompositionLocalProvider(LocalBottomBarClearance provides 92.dp) {
            HistoryContent(state = HistoryUiState.Success(songs = previewSongs), onRetry = {}, onOpenSong = {})
        }
    }
}