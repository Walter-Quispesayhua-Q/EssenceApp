package com.essence.essenceapp.feature.playlist.ui.detail.componets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.essence.essenceapp.feature.playlist.domain.model.Playlist
import com.essence.essenceapp.feature.playlist.ui.detail.PlaylistDetailAction
import com.essence.essenceapp.feature.playlist.ui.detail.PlaylistDetailUiState
import com.essence.essenceapp.feature.song.domain.model.SongSimple
import com.essence.essenceapp.shared.ui.components.cards.BaseCard
import com.essence.essenceapp.shared.ui.components.cards.song.CompactSongContent
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val AccentColor = Color(0xFF00CED1)
private val HorizontalMargin = 20.dp
private val SectionGap = 24.dp
private val CoverSize = 220.dp
private val CoverRadius = 24.dp

private val SpanishDateFormatter =
    DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("es", "ES"))

@Composable
fun PlaylistDetailContent(
    modifier: Modifier = Modifier,
    state: PlaylistDetailUiState,
    onAction: (PlaylistDetailAction) -> Unit,
    onRetry: () -> Unit = {},
    onShare: () -> Unit = {}
) {
    when (state) {
        PlaylistDetailUiState.Loading -> DetailLoadingState(modifier)
        is PlaylistDetailUiState.Error -> DetailErrorState(
            modifier = modifier,
            message = state.message,
            onRetry = onRetry
        )
        is PlaylistDetailUiState.Success -> DetailSuccessState(
            modifier = modifier,
            playlist = state.playlist,
            songs = state.songs,
            isSongsLoading = state.isSongsLoading,
            onAction = onAction,
            onShare = onShare
        )
    }
}

@Composable
private fun DetailSuccessState(
    modifier: Modifier,
    playlist: Playlist,
    songs: List<SongSimple>,
    isSongsLoading: Boolean,
    onAction: (PlaylistDetailAction) -> Unit,
    onShare: () -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = HorizontalMargin,
            end = HorizontalMargin,
            top = 8.dp,
            bottom = 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(SectionGap)
    ) {
        item { HeroSection(playlist = playlist) }

        item {
            ActionsSection(
                isPublic = playlist.isPublic,
                onEdit = { onAction(PlaylistDetailAction.EditPlaylist) },
                onDelete = { onAction(PlaylistDetailAction.DeletePlaylist) },
                onShare = onShare
            )
        }

        item {
            Text(
                text = "Canciones",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        when {
            isSongsLoading -> {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            }

            songs.isEmpty() -> {
                item {
                    BaseCard(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(14.dp)
                    ) {
                        Text(
                            text = "Esta playlist no tiene canciones todavía.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f)
                        )
                    }
                }
            }

            else -> {
                items(songs, key = { it.id }) { song ->
                    SongRow(
                        song = song,
                        isPublic = playlist.isPublic,
                        onRemove = { onAction(PlaylistDetailAction.RemoveSong(song.id)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroSection(playlist: Playlist) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CoverArt(imageKey = playlist.imageKey)

        Text(
            text = playlist.title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        if (!playlist.description.isNullOrBlank()) {
            Text(
                text = playlist.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.82f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            VisibilityBadge(isPublic = playlist.isPublic)

            Text(
                text = "${playlist.totalSongs} canciones",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.86f)
            )
        }

        Text(
            text = "Actualizada: ${(playlist.updatedAt ?: playlist.createdAt).format(SpanishDateFormatter)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f)
        )
    }
}

@Composable
private fun CoverArt(imageKey: String?) {
    Box(
        modifier = Modifier.size(CoverSize),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(CoverRadius))
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            AccentColor.copy(alpha = 0.30f),
                            Color.Transparent
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(CoverRadius))
                .background(MaterialTheme.colorScheme.surface)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.20f),
                    RoundedCornerShape(CoverRadius)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlaylistPlay,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(72.dp)
            )
        }
    }
}

@Composable
private fun VisibilityBadge(isPublic: Boolean) {
    val label = if (isPublic) "Pública" else "Privada"
    val container = if (isPublic) {
        MaterialTheme.colorScheme.secondary.copy(alpha = 0.22f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(container)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ActionsSection(
    isPublic: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FilledTonalButton(
            onClick = onEdit,
            modifier = Modifier.weight(1f)
        ) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = null)
            Spacer(modifier = Modifier.size(6.dp))
            Text("Editar")
        }

        if (isPublic) {
            OutlinedButton(
                onClick = onShare,
                modifier = Modifier.weight(1f)
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.size(6.dp))
                Text("Compartir")
            }
        }

        FilledTonalButton(
            onClick = onDelete,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = null)
            Spacer(modifier = Modifier.size(6.dp))
            Text("Eliminar")
        }
    }
}

@Composable
private fun SongRow(
    song: SongSimple,
    isPublic: Boolean,
    onRemove: () -> Unit
) {
    BaseCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                CompactSongContent(
                    song = song,
                    durationText = formatDuration(song.durationMs.toLong())
                )
            }

            if (!isPublic) {
                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Quitar canción",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailLoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CircularProgressIndicator(modifier = Modifier.size(28.dp))
            Text(
                text = "Cargando detalle...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f)
            )
        }
    }
}

@Composable
private fun DetailErrorState(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize().padding(horizontal = HorizontalMargin),
        contentAlignment = Alignment.Center
    ) {
        BaseCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "No se pudo cargar la playlist",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
                Button(onClick = onRetry, modifier = Modifier.fillMaxWidth()) {
                    Text("Reintentar")
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

private val previewPlaylist = Playlist(
    id = 1L,
    title = "Noches Lo-Fi",
    description = "Selección para estudiar y concentrarse.",
    imageKey = null,
    isPublic = false,
    totalSongs = 3,
    createdAt = LocalDate.of(2025, 11, 20),
    updatedAt = LocalDate.of(2026, 3, 2),
    totalLikes = 42
)

private val previewSongs = listOf(
    SongSimple(1L, "Tití Me Preguntó", 210_000, "", null, "single", 1_200_000L, "Bad Bunny", "Un Verano Sin Ti", LocalDate.of(2022, 5, 6)),
    SongSimple(2L, "Ojitos Lindos", 245_000, "", null, "single", 900_000L, "Bad Bunny", "Un Verano Sin Ti", LocalDate.of(2022, 5, 6)),
    SongSimple(3L, "Efecto", 198_000, "", null, "single", 750_000L, "Bad Bunny", "Un Verano Sin Ti", LocalDate.of(2022, 5, 6))
)

@Preview(name = "Loading", showBackground = true)
@Composable
private fun LoadingPreview() {
    EssenceAppTheme {
        PlaylistDetailContent(state = PlaylistDetailUiState.Loading, onAction = {})
    }
}

@Preview(name = "Error", showBackground = true)
@Composable
private fun ErrorPreview() {
    EssenceAppTheme {
        PlaylistDetailContent(state = PlaylistDetailUiState.Error("Sin conexión"), onAction = {})
    }
}

@Preview(name = "Success con canciones", showBackground = true)
@Composable
private fun SuccessWithSongsPreview() {
    EssenceAppTheme {
        PlaylistDetailContent(
            state = PlaylistDetailUiState.Success(
                playlist = previewPlaylist,
                songs = previewSongs,
                isSongsLoading = false
            ),
            onAction = {}
        )
    }
}

@Preview(name = "Success sin canciones", showBackground = true)
@Composable
private fun SuccessEmptyPreview() {
    EssenceAppTheme {
        PlaylistDetailContent(
            state = PlaylistDetailUiState.Success(
                playlist = previewPlaylist,
                songs = emptyList(),
                isSongsLoading = false
            ),
            onAction = {}
        )
    }
}
