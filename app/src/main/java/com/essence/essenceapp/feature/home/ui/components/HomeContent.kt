package com.essence.essenceapp.feature.home.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.album.domain.model.AlbumSimple
import com.essence.essenceapp.feature.artist.domain.model.ArtistSimple
import com.essence.essenceapp.feature.home.ui.HomeUiState
import com.essence.essenceapp.feature.home.ui.preview.SampleData
import com.essence.essenceapp.feature.song.domain.model.SongSimple
import com.essence.essenceapp.shared.ui.components.cards.album.GridAlbumContent
import com.essence.essenceapp.shared.ui.components.cards.artist.CircleArtistContent
import com.essence.essenceapp.shared.ui.components.cards.song.CompactSongContent
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Icon

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    state: HomeUiState,
    onRefresh: () -> Unit
) {
    when (state) {
        is HomeUiState.Loading -> {
            LoadingContent(modifier = modifier)
        }

        is HomeUiState.Success -> {
            HomeSuccessContent(
                modifier = modifier,
                songs = state.homeData.songs,
                albums = state.homeData.albums,
                artists = state.homeData.artists
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
private fun LoadingContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
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
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Ocurrió un error",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onRetry) {
            Text("Reintentar")
        }
    }
}

@Composable
private fun HomeSuccessContent(
    songs: List<SongSimple>,
    albums: List<AlbumSimple>,
    artists: List<ArtistSimple>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            HomeSectionTitle(
                title = "Canciones",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        item {
            SongsSection(
                songs = songs,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            HomeSectionTitle(
                title = "Álbumes",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item {
            AlbumsSection(
                albums = albums,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            HomeSectionTitle(
                title = "Artistas",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item {
            ArtistsSection(
                artists = artists,
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
    Text(
        text = title,
        modifier = modifier,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun SongsSection(
    songs: List<SongSimple>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = songs,
            key = { it.id }
        ) { song ->
            Box(
                modifier = Modifier.width(280.dp)
            ) {
                CompactSongContent(song = song)
            }
        }
    }
}

@Composable
private fun AlbumsSection(
    albums: List<AlbumSimple>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = albums,
            key = { it.id }
        ) { album ->
            Box(
                modifier = Modifier.width(140.dp)
            ) {
                GridAlbumContent(album = album)
            }
        }
    }
}

@Composable
private fun ArtistsSection(
    artists: List<ArtistSimple>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = artists,
            key = { it.id }
        ) { artist ->
            Box(
                modifier = Modifier.width(120.dp)
            ) {
                CircleArtistContent(artist = artist)
            }
        }
    }
}

@Preview(name = "Home - Success", showBackground = true)
@Composable
private fun HomeContentLoadedPreview() {
    EssenceAppTheme {
        HomeContent(
            state = HomeUiState.Success(
                homeData = SampleData.home
            ),
            onRefresh = {}
        )
    }
}

@Preview(name = "Home - Loading", showBackground = true)
@Composable
private fun HomeContentLoadingPreview() {
    EssenceAppTheme {
        HomeContent(
            state = HomeUiState.Loading,
            onRefresh = {}
        )
    }
}

@Preview(name = "Home - Error", showBackground = true)
@Composable
private fun HomeContentErrorPreview() {
    EssenceAppTheme {
        HomeContent(
            state = HomeUiState.Error(message = "Error al cargar datos"),
            onRefresh = {}
        )
    }
}