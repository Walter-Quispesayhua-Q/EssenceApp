package com.essence.essenceapp.feature.artist.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.essence.essenceapp.feature.album.domain.model.AlbumSimple
import com.essence.essenceapp.feature.artist.domain.model.Artist
import com.essence.essenceapp.feature.artist.ui.ArtistDetailAction
import com.essence.essenceapp.feature.artist.ui.ArtistDetailUiState
import com.essence.essenceapp.feature.song.domain.model.SongSimple
import com.essence.essenceapp.shared.ui.components.cards.BaseCard
import com.essence.essenceapp.shared.ui.components.cards.album.GridAlbumContent
import com.essence.essenceapp.shared.ui.components.cards.song.CompactSongContent
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import java.time.LocalDate
import java.util.Locale

private val AccentColor = Color(0xFF00CED1)
private val HorizontalMargin = 20.dp
private val SectionGap = 32.dp

@Composable
fun ArtistDetailContent(
    modifier: Modifier = Modifier,
    state: ArtistDetailUiState,
    onAction: (ArtistDetailAction) -> Unit
) {
    when (state) {
        ArtistDetailUiState.Loading -> ArtistDetailLoadingState(modifier = modifier)

        is ArtistDetailUiState.Error -> ArtistDetailErrorState(
            modifier = modifier,
            message = state.message,
            onRetry = { onAction(ArtistDetailAction.Refresh) }
        )

        is ArtistDetailUiState.Success -> ArtistDetailSuccessState(
            modifier = modifier,
            artist = state.artist,
            onAction = onAction
        )
    }
}

@Composable
private fun ArtistDetailLoadingState(modifier: Modifier = Modifier) {
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
                text = "Cargando artista...",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
            )
        }
    }
}

@Composable
private fun ArtistDetailErrorState(
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
                    text = "No se pudo cargar el artista",
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
private fun ArtistDetailSuccessState(
    modifier: Modifier = Modifier,
    artist: Artist,
    onAction: (ArtistDetailAction) -> Unit
) {
    val songs = artist.songs.orEmpty()
    val albums = artist.albums
    val totalPlays = songs.sumOf { (it.totalPlays ?: 0L).toLong() }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(SectionGap)
    ) {
        item {
            if (albums != null) {
                ArtistHeroSection(
                    artist = artist,
                    songsCount = songs.size,
                    albumsCount = albums.size
                )
            }
        }

        item {
            ArtistStatsSection(
                totalPlays = totalPlays,
                songsCount = songs.size
            )
        }

        item {
            ArtistActionsSection(
                isEnabled = songs.isNotEmpty(),
                onPlay = {
                    songs.firstOrNull()?.let { onAction(ArtistDetailAction.OpenSong(it.id)) }
                },
                onShuffle = {
                    songs.shuffled().firstOrNull()?.let { onAction(ArtistDetailAction.OpenSong(it.id)) }
                }
            )
        }

        item {
            TracklistSection(
                songs = songs,
                onOpenSong = { onAction(ArtistDetailAction.OpenSong(it)) }
            )
        }

        item {
            if (albums != null) {
                AlbumsSection(
                    albums = albums,
                    onOpenAlbum = { onAction(ArtistDetailAction.OpenAlbum(it)) }
                )
            }
        }
    }
}

@Composable
private fun ArtistHeroSection(
    artist: Artist,
    songsCount: Int,
    albumsCount: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp)
            .padding(horizontal = HorizontalMargin)
            .clip(RoundedCornerShape(24.dp))
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.55f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.38f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0.55f)
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color.Transparent,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
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
            VerifiedBadge()

            Text(
                text = artist.nameArtist,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MetaPill(text = "$songsCount canciones")
                MetaPill(text = "$albumsCount álbumes")
                artist.country?.takeIf { it.isNotBlank() }?.let { country ->
                    MetaPill(text = country)
                }
            }

            artist.description?.takeIf { it.isNotBlank() }?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.75f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun VerifiedBadge() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(AccentColor.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = AccentColor,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = "Artista verificado",
            style = MaterialTheme.typography.labelMedium,
            color = AccentColor
        )
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
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ArtistStatsSection(
    totalPlays: Long,
    songsCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HorizontalMargin),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            label = "PLAYS",
            value = formatPlays(totalPlays)
        )
        StatCard(
            modifier = Modifier.weight(1f),
            label = "META",
            value = "$songsCount Songs"
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    BaseCard(
        modifier = modifier,
        contentPadding = PaddingValues(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ArtistActionsSection(
    isEnabled: Boolean,
    onPlay: () -> Unit,
    onShuffle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HorizontalMargin),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            modifier = Modifier.weight(1f),
            onClick = onPlay,
            enabled = isEnabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentColor,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        ) {
            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("PLAY")
        }

        OutlinedButton(
            modifier = Modifier.weight(1f),
            onClick = onShuffle,
            enabled = isEnabled
        ) {
            Icon(imageVector = Icons.Default.Shuffle, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("SHUFFLE")
        }
    }
}

@Composable
private fun TracklistSection(
    songs: List<SongSimple>,
    onOpenSong: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HorizontalMargin),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Tracks",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (songs.isEmpty()) {
            BaseCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(14.dp)
            ) {
                Text(
                    text = "No hay canciones disponibles.",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )
            }
            return
        }

        songs.forEachIndexed { index, song ->
            val isPlaying = index == 0

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "${index + 1}".padStart(2, '0'),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isPlaying) AccentColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                    modifier = Modifier.width(26.dp)
                )

                BaseCard(
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            width = if (isPlaying) 1.dp else 0.dp,
                            color = AccentColor.copy(alpha = 0.55f),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { onOpenSong(song.id) },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CompactSongContent(
                            song = song,
                            modifier = Modifier.weight(1f),
                            durationText = formatDuration(song.durationMs)
                        )
                        IconButton(onClick = { onOpenSong(song.id) }) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Abrir canción"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AlbumsSection(
    albums: List<AlbumSimple>,
    onOpenAlbum: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HorizontalMargin),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Álbumes",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (albums.isEmpty()) {
            BaseCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(14.dp)
            ) {
                Text(
                    text = "No hay álbumes disponibles.",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )
            }
            return
        }

        val rows = albums.chunked(2)
        rows.forEach { rowAlbums ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowAlbums.forEach { album ->
                    BaseCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onOpenAlbum(album.id) },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            GridAlbumContent(album = album)
                        }
                    }
                }
                if (rowAlbums.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

private fun formatDuration(durationMs: Int): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

private fun formatPlays(value: Long): String {
    return when {
        value >= 1_000_000_000L -> String.format(Locale.US, "%.1fB", value / 1_000_000_000f)
        value >= 1_000_000L -> String.format(Locale.US, "%.1fM", value / 1_000_000f)
        value >= 1_000L -> String.format(Locale.US, "%.1fK", value / 1_000f)
        else -> value.toString()
    }
}

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
        title = "Moscow Mule",
        durationMs = 245_000,
        hlsMasterKey = "songs/moscow/master.m3u8",
        imageKey = null,
        songType = "album",
        totalPlays = 980_000L,
        artistName = "Bad Bunny",
        albumName = "Un Verano Sin Ti",
        releaseDate = LocalDate.of(2022, 5, 6)
    )
)

private val previewAlbums = listOf(
    AlbumSimple(
        id = 10L,
        title = "Un Verano Sin Ti",
        imageKey = null,
        albumUrl = "albums/un-verano-sin-ti",
        artists = listOf("Bad Bunny"),
        release = LocalDate.of(2022, 5, 6)
    ),
    AlbumSimple(
        id = 11L,
        title = "Nadie Sabe Lo Que Va a Pasar Mañana",
        imageKey = null,
        albumUrl = "albums/nslqvpm",
        artists = listOf("Bad Bunny"),
        release = LocalDate.of(2023, 10, 13)
    ),
    AlbumSimple(
        id = 12L,
        title = "YHLQMDLG",
        imageKey = null,
        albumUrl = "albums/yhlqmdlg",
        artists = listOf("Bad Bunny"),
        release = LocalDate.of(2020, 2, 29)
    )
)

private val previewArtist = Artist(
    id = 100L,
    nameArtist = "Bad Bunny",
    description = "Uno de los artistas más influyentes de la música latina actual.",
    imageKey = null,
    artistUrl = "artists/bad-bunny",
    country = "Puerto Rico",
    songs = previewSongs,
    albums = previewAlbums
)

@Preview(name = "Artist Detail - Loading", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ArtistDetailContentLoadingPreview() {
    EssenceAppTheme {
        ArtistDetailContent(
            state = ArtistDetailUiState.Loading,
            onAction = {}
        )
    }
}

@Preview(name = "Artist Detail - Error", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ArtistDetailContentErrorPreview() {
    EssenceAppTheme {
        ArtistDetailContent(
            state = ArtistDetailUiState.Error("Sin conexión"),
            onAction = {}
        )
    }
}

@Preview(name = "Artist Detail - Success", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ArtistDetailContentSuccessPreview() {
    EssenceAppTheme {
        ArtistDetailContent(
            state = ArtistDetailUiState.Success(previewArtist),
            onAction = {}
        )
    }
}
