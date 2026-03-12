package com.essence.essenceapp.feature.song.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.essence.essenceapp.feature.album.domain.model.AlbumSimple
import com.essence.essenceapp.feature.artist.domain.model.ArtistSimple
import com.essence.essenceapp.feature.song.domain.model.Song
import com.essence.essenceapp.feature.song.ui.SongDetailAction
import com.essence.essenceapp.feature.song.ui.SongDetailUiState
import com.essence.essenceapp.feature.song.ui.manager.PlaybackUiState
import com.essence.essenceapp.feature.song.ui.manager.SongDetailManagerAction
import com.essence.essenceapp.shared.ui.components.cards.BaseCard
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import java.time.LocalDate
import kotlin.math.max

private val AccentColor = Color(0xFF00CED1)
private val HorizontalMargin = 20.dp
private val SectionGap = 24.dp

@Composable
fun SongDetailContent(
    modifier: Modifier = Modifier,
    state: SongDetailUiState,
    onAction: (SongDetailAction) -> Unit,
    onManagerAction: (SongDetailManagerAction) -> Unit
) {
    when (state) {
        SongDetailUiState.Loading -> LoadingState(modifier = modifier)

        is SongDetailUiState.Error -> ErrorState(
            modifier = modifier,
            message = state.message,
            onRetry = { onAction(SongDetailAction.Refresh) }
        )

        is SongDetailUiState.Success -> SuccessState(
            modifier = modifier,
            song = state.song,
            playback = state.playback,
            onAction = onAction,
            onManagerAction = onManagerAction
        )
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = "Cargando canción...",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
            )
        }
    }
}

@Composable
private fun ErrorState(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = HorizontalMargin),
        contentAlignment = Alignment.Center
    ) {
        BaseCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "No se pudo cargar la canción",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onRetry
                ) {
                    Text("Reintentar")
                }
            }
        }
    }
}

@Composable
private fun SuccessState(
    modifier: Modifier = Modifier,
    song: Song,
    playback: PlaybackUiState,
    onAction: (SongDetailAction) -> Unit,
    onManagerAction: (SongDetailManagerAction) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(SectionGap)
    ) {
        HeroSection(song = song)

        PlaybackSection(
            playback = playback,
            fallbackDurationMs = song.durationMs.toLong(),
            onManagerAction = onManagerAction
        )

        RelationsSection(
            song = song,
            onAction = onAction
        )
    }
}

@Composable
private fun HeroSection(song: Song) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(360.dp)
            .padding(horizontal = HorizontalMargin)
            .clip(RoundedCornerShape(24.dp))
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.50f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0.90f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = song.artists.joinToString(", ") { it.nameArtist },
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.75f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetaPill(text = formatDuration(song.durationMs.toLong()))
                song.songType?.takeIf { it.isNotBlank() }?.let { MetaPill(text = it) }
                song.releaseDate?.let { MetaPill(text = it.year.toString()) }
            }
        }
    }
}

@Composable
private fun MetaPill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun PlaybackSection(
    playback: PlaybackUiState,
    fallbackDurationMs: Long,
    onManagerAction: (SongDetailManagerAction) -> Unit
) {
    val durationMs = max(playback.durationMs, fallbackDurationMs).coerceAtLeast(1L)
    val positionMs = playback.positionMs.coerceIn(0L, durationMs)

    BaseCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HorizontalMargin),
        contentPadding = PaddingValues(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Reproducción",
                style = MaterialTheme.typography.titleMedium
            )

            Slider(
                value = positionMs.toFloat(),
                onValueChange = { onManagerAction(SongDetailManagerAction.SeekTo(it.toLong())) },
                valueRange = 0f..durationMs.toFloat()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatDuration(positionMs), style = MaterialTheme.typography.bodySmall)
                Text(formatDuration(durationMs), style = MaterialTheme.typography.bodySmall)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextButton(
                    modifier = Modifier.weight(1f),
                    onClick = { onManagerAction(SongDetailManagerAction.Previous) }
                ) { Text("Previous") }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onManagerAction(
                            if (playback.isPlaying) SongDetailManagerAction.Pause
                            else SongDetailManagerAction.Play
                        )
                    }
                ) {
                    Text(if (playback.isPlaying) "Pause" else "Play")
                }

                TextButton(
                    modifier = Modifier.weight(1f),
                    onClick = { onManagerAction(SongDetailManagerAction.Next) }
                ) { Text("Next") }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextButton(
                    modifier = Modifier.weight(1f),
                    onClick = { onManagerAction(SongDetailManagerAction.SeekBy(-10_000L)) }
                ) { Text("-10s") }

                TextButton(
                    modifier = Modifier.weight(1f),
                    onClick = { onManagerAction(SongDetailManagerAction.SeekBy(10_000L)) }
                ) { Text("+10s") }

                TextButton(
                    modifier = Modifier.weight(1f),
                    onClick = { onManagerAction(SongDetailManagerAction.Stop) }
                ) { Text("Stop") }
            }
        }
    }
}

@Composable
private fun RelationsSection(
    song: Song,
    onAction: (SongDetailAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HorizontalMargin),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Detalles",
            style = MaterialTheme.typography.titleLarge
        )

        if (song.album != null) {
            RelationCard(
                icon = Icons.Default.Album,
                title = "Álbum",
                subtitle = song.album.title,
                onClick = { onAction(SongDetailAction.OpenAlbum(song.album.id)) }
            )
        }

        song.artists.forEach { artist ->
            RelationCard(
                icon = Icons.Default.Person,
                title = "Artista",
                subtitle = artist.nameArtist,
                onClick = { onAction(SongDetailAction.OpenArtist(artist.id)) }
            )
        }

        RelationCard(
            icon = Icons.Default.MusicNote,
            title = "Tipo",
            subtitle = song.songType ?: "No definido",
            onClick = {}
        )
    }
}

@Composable
private fun RelationCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    BaseCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AccentColor
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.70f)
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
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

private val previewSong = Song(
    id = 1L,
    title = "Tití Me Preguntó",
    durationMs = 210_000,
    hlsMasterKey = "songs/titi/master.m3u8",
    imageKey = null,
    songType = "single",
    totalPlays = 1_200_000,
    artists = listOf(
        ArtistSimple(id = 10L, nameArtist = "Bad Bunny", imageKey = null, artistUrl = "artists/bad-bunny")
    ),
    album = AlbumSimple(
        id = 20L,
        title = "Un Verano Sin Ti",
        imageKey = null,
        albumUrl = "albums/un-verano-sin-ti",
        artists = listOf("Bad Bunny"),
        release = LocalDate.of(2022, 5, 6)
    ),
    releaseDate = LocalDate.of(2022, 5, 6)
)

@Preview(name = "Song Detail - Loading", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun SongDetailContentLoadingPreview() {
    EssenceAppTheme {
        SongDetailContent(
            state = SongDetailUiState.Loading,
            onAction = {},
            onManagerAction = {}
        )
    }
}

@Preview(name = "Song Detail - Error", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun SongDetailContentErrorPreview() {
    EssenceAppTheme {
        SongDetailContent(
            state = SongDetailUiState.Error("Sin conexión"),
            onAction = {},
            onManagerAction = {}
        )
    }
}

@Preview(name = "Song Detail - Success", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun SongDetailContentSuccessPreview() {
    EssenceAppTheme {
        SongDetailContent(
            state = SongDetailUiState.Success(
                song = previewSong,
                playback = PlaybackUiState(
                    isPlaying = true,
                    positionMs = 72_000L,
                    durationMs = 210_000L
                )
            ),
            onAction = {},
            onManagerAction = {}
        )
    }
}
