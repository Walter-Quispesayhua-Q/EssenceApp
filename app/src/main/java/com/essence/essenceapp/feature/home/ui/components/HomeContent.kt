package com.essence.essenceapp.feature.home.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.MusicOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.album.domain.model.AlbumSimple
import com.essence.essenceapp.feature.artist.domain.model.ArtistSimple
import com.essence.essenceapp.feature.home.ui.HomeUiState
import com.essence.essenceapp.feature.home.ui.preview.SampleData
import com.essence.essenceapp.feature.song.domain.model.SongSimple
import com.essence.essenceapp.shared.ui.components.cards.BaseCard
import com.essence.essenceapp.shared.ui.components.cards.album.GridAlbumContent
import com.essence.essenceapp.shared.ui.components.cards.artist.CircleArtistContent
import com.essence.essenceapp.shared.ui.components.cards.song.CompactSongContent
import com.essence.essenceapp.ui.shell.LocalBottomBarClearance
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.SoftRose

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    state: HomeUiState,
    isLoggedIn: Boolean,
    onRefresh: () -> Unit,
    onOpenSong: (String) -> Unit,
    onOpenAlbum: (String) -> Unit,
    onOpenArtist: (String) -> Unit
) {
    when (state) {
        is HomeUiState.Loading -> LoadingContent(modifier = modifier)
        is HomeUiState.Success -> {
            HomeSuccessContent(
                modifier = modifier,
                songs = state.homeData.songs,
                albums = state.homeData.albums,
                artists = state.homeData.artists,
                recentSongs = state.recentSongs,
                isLoggedIn = isLoggedIn,
                onOpenSong = onOpenSong,
                onOpenAlbum = onOpenAlbum,
                onOpenArtist = onOpenArtist
            )
        }
        is HomeUiState.Error -> {
            ErrorContent(
                modifier = modifier,
                message = state.message,
                onRetry = onRefresh
            )
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    val brush = rememberShimmerBrush()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        ShimmerBox(
            modifier = Modifier
                .width(180.dp)
                .height(18.dp),
            brush = brush
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                repeat(4) { SkeletonSongRow(brush = brush) }
            }
        }

        ShimmerBox(
            modifier = Modifier
                .width(200.dp)
                .height(18.dp),
            brush = brush
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(3) { SkeletonAlbumCard(brush = brush) }
        }

        ShimmerBox(
            modifier = Modifier
                .width(190.dp)
                .height(18.dp),
            brush = brush
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(3) { SkeletonArtistCircle(brush = brush) }
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(72.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.ErrorOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Algo salió mal",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = onRetry,
            shape = RoundedCornerShape(50),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "Reintentar",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun HomeSuccessContent(
    songs: List<SongSimple>,
    albums: List<AlbumSimple>,
    artists: List<ArtistSimple>,
    recentSongs: List<SongSimple>,
    isLoggedIn: Boolean,
    onOpenSong: (String) -> Unit,
    onOpenAlbum: (String) -> Unit,
    onOpenArtist: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val bottomClearance = LocalBottomBarClearance.current

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = bottomClearance + 16.dp)
    ) {
        if (isLoggedIn) {
            item {
                HomeSectionTitle(
                    title = "Escuchadas recientemente",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            if (recentSongs.isEmpty()) {
                item {
                    EmptySectionCard(
                        message = "Aún no tienes canciones recientes.",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            } else {
                item {
                    SongsIsland(
                        songs = recentSongs,
                        maxItems = 10,
                        onOpenSong = onOpenSong,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }

        item {
            HomeSectionTitle(
                title = "Canciones más escuchadas",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item {
            SongsIsland(
                songs = songs,
                maxItems = 5,
                onOpenSong = onOpenSong,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item {
            HomeSectionTitle(
                title = "Álbumes más escuchados",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item {
            AlbumsSection(
                albums = albums,
                onOpenAlbum = onOpenAlbum,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            HomeSectionTitle(
                title = "Artistas más escuchados",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item {
            ArtistsSection(
                artists = artists,
                onOpenArtist = onOpenArtist,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun HomeSectionTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(18.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SoftRose,
                            SoftRose.copy(alpha = 0.3f)
                        )
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
    }
}

@Composable
private fun EmptySectionCard(
    message: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.MusicOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun SongsIsland(
    songs: List<SongSimple>,
    maxItems: Int,
    onOpenSong: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            val displaySongs = songs.take(maxItems)
            displaySongs.forEachIndexed { index, song ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenSong(song.detailLookup) }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    CompactSongContent(song = song)
                }

                if (index < displaySongs.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 14.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
                    )
                }
            }
        }
    }
}

@Composable
private fun AlbumsSection(
    albums: List<AlbumSimple>,
    onOpenAlbum: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(albums, key = { it.detailLookup }) { album ->
            BaseCard(
                modifier = Modifier
                    .width(140.dp)
                    .clickable { onOpenAlbum(album.detailLookup) }
            ) {
                GridAlbumContent(album = album)
            }
        }
    }
}

@Composable
private fun ArtistsSection(
    artists: List<ArtistSimple>,
    onOpenArtist: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(artists, key = { it.detailLookup }) { artist ->
            BaseCard(
                modifier = Modifier
                    .width(120.dp)
                    .clickable { onOpenArtist(artist.detailLookup) }
            ) {
                CircleArtistContent(artist = artist)
            }
        }
    }
}

@Composable
private fun rememberShimmerBrush(): Brush {
    val shimmerColors = listOf(
        GraphiteSurface.copy(alpha = 0.6f),
        GraphiteSurface.copy(alpha = 0.2f),
        GraphiteSurface.copy(alpha = 0.6f)
    )
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_anim"
    )
    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 300f, translateAnim - 300f),
        end = Offset(translateAnim, translateAnim)
    )
}

@Composable
private fun ShimmerBox(
    modifier: Modifier = Modifier,
    brush: Brush,
    shape: Shape = RoundedCornerShape(6.dp)
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(brush)
    )
}

@Composable
private fun SkeletonSongRow(brush: Brush) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShimmerBox(
            modifier = Modifier.size(45.dp),
            brush = brush,
            shape = RoundedCornerShape(8.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            ShimmerBox(
                modifier = Modifier
                    .width(160.dp)
                    .height(14.dp),
                brush = brush
            )
            ShimmerBox(
                modifier = Modifier
                    .width(100.dp)
                    .height(11.dp),
                brush = brush
            )
        }
    }
}

@Composable
private fun SkeletonAlbumCard(brush: Brush) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ShimmerBox(
            modifier = Modifier.size(100.dp),
            brush = brush,
            shape = RoundedCornerShape(12.dp)
        )
        ShimmerBox(
            modifier = Modifier
                .width(80.dp)
                .height(12.dp),
            brush = brush
        )
    }
}

@Composable
private fun SkeletonArtistCircle(brush: Brush) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ShimmerBox(
            modifier = Modifier.size(80.dp),
            brush = brush,
            shape = CircleShape
        )
        ShimmerBox(
            modifier = Modifier
                .width(60.dp)
                .height(12.dp),
            brush = brush
        )
    }
}

@Preview(name = "Home - Success", showBackground = true)
@Composable
private fun HomeContentLoadedPreview() {
    EssenceAppTheme {
        HomeContent(
            state = HomeUiState.Success(
                homeData = SampleData.home,
                recentSongs = SampleData.home.songs.take(3)
            ),
            isLoggedIn = true,
            onRefresh = {},
            onOpenSong = {},
            onOpenAlbum = {},
            onOpenArtist = {}
        )
    }
}

@Preview(name = "Home - Loading", showBackground = true)
@Composable
private fun HomeContentLoadingPreview() {
    EssenceAppTheme {
        HomeContent(
            state = HomeUiState.Loading,
            isLoggedIn = false,
            onRefresh = {},
            onOpenSong = {},
            onOpenAlbum = {},
            onOpenArtist = {}
        )
    }
}

@Preview(name = "Home - Error", showBackground = true)
@Composable
private fun HomeContentErrorPreview() {
    EssenceAppTheme {
        HomeContent(
            state = HomeUiState.Error(message = "Error al cargar datos"),
            isLoggedIn = false,
            onRefresh = {},
            onOpenSong = {},
            onOpenAlbum = {},
            onOpenArtist = {}
        )
    }
}