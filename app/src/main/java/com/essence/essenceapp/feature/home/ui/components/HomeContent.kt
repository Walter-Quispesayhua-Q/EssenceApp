package com.essence.essenceapp.feature.home.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.MusicOff
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.album.domain.model.AlbumSimple
import com.essence.essenceapp.feature.artist.domain.model.ArtistSimple
import com.essence.essenceapp.feature.home.ui.HomeUiState
import com.essence.essenceapp.feature.home.ui.preview.SampleData
import com.essence.essenceapp.feature.song.domain.model.SongSimple
import com.essence.essenceapp.shared.ui.components.status.error.AppErrorState
import com.essence.essenceapp.shared.ui.components.cards.album.GridAlbumContent
import com.essence.essenceapp.shared.ui.components.cards.artist.CircleArtistContent
import com.essence.essenceapp.ui.shell.LocalBottomBarClearance
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.LuxeGold
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose
import com.essence.essenceapp.shared.playback.model.PlaybackOpenRequest

private const val MAX_HOME_ITEMS = 10
private val SECTION_SPACING = 24.dp
private val SECTION_SPACING_TIGHT = 4.dp
private val SECTION_INTERNAL_SPACING = 12.dp

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    state: HomeUiState,
    isLoggedIn: Boolean,
    onRefresh: () -> Unit,
    onOpenSong: (PlaybackOpenRequest) -> Unit,
    onOpenAlbum: (String) -> Unit,
    onOpenArtist: (String) -> Unit
) {
    AnimatedContent(
        targetState = state,
        modifier = modifier,
        contentKey = { it::class },
        transitionSpec = {
            fadeIn(animationSpec = tween(durationMillis = 220))
                .togetherWith(fadeOut(animationSpec = tween(durationMillis = 160)))
        },
        label = "home_state_transition"
    ) { currentState ->
        when (currentState) {
            is HomeUiState.Loading -> HomeSkeleton()
            is HomeUiState.Success -> {
                HomeSuccessContent(
                    songs = currentState.homeData.songs,
                    albums = currentState.homeData.albums,
                    artists = currentState.homeData.artists,
                    recentSongs = currentState.recentSongs,
                    isLoggedIn = isLoggedIn,
                    isRefreshing = currentState.isRefreshing,
                    onOpenSong = onOpenSong,
                    onOpenAlbum = onOpenAlbum,
                    onOpenArtist = onOpenArtist
                )
            }
            is HomeUiState.Error -> AppErrorState(
                message = currentState.message,
                onRetry = onRefresh
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
    isRefreshing: Boolean,
    onOpenSong: (PlaybackOpenRequest) -> Unit,
    onOpenAlbum: (String) -> Unit,
    onOpenArtist: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val bottomClearance = LocalBottomBarClearance.current
    val scrollState = rememberScrollState()
    val showHistorySection = isLoggedIn && recentSongs.isNotEmpty()
    val showSongsSection = songs.isNotEmpty()
    val showAlbumsSection = albums.isNotEmpty()
    val showArtistsSection = artists.isNotEmpty()

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(SECTION_SPACING)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            if (showHistorySection) {
                HomeSection(
                    icon = Icons.Outlined.History,
                    title = "Vuelve a escuchar",
                    accent = MutedTeal
                ) {
                    SongsPagerSection(
                        songs = recentSongs.take(MAX_HOME_ITEMS),
                        sourceKey = "home:recent",
                        accent = MutedTeal,
                        onOpenSong = onOpenSong
                    )
                }
            }

            if (showSongsSection) {
                HomeSection(
                    icon = Icons.Outlined.TrendingUp,
                    title = "Top Canciones",
                    accent = SoftRose
                ) {
                    SongsPagerSection(
                        songs = songs.take(MAX_HOME_ITEMS),
                        sourceKey = "home:top",
                        accent = SoftRose,
                        onOpenSong = onOpenSong,
                        showRank = true
                    )
                }
            }

            if (showAlbumsSection || showArtistsSection) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(SECTION_SPACING_TIGHT)
                ) {
                    if (showAlbumsSection) {
                        HomeSection(
                            icon = Icons.Outlined.Album,
                            title = "Top Álbumes",
                            accent = LuxeGold
                        ) {
                            AlbumsSection(
                                albums = albums.take(MAX_HOME_ITEMS),
                                onOpenAlbum = onOpenAlbum,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    if (showArtistsSection) {
                        HomeSection(
                            icon = Icons.Outlined.Person,
                            title = "Top Artistas",
                            accent = MutedTeal
                        ) {
                            ArtistsSection(
                                artists = artists.take(MAX_HOME_ITEMS),
                                onOpenArtist = onOpenArtist,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(bottomClearance + 16.dp))
        }

        if (isRefreshing) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.5.dp)
                    .align(Alignment.TopCenter),
                color = SoftRose.copy(alpha = 0.65f),
                trackColor = Color.Transparent
            )
        }
    }
}

@Composable
private fun HomeSection(
    icon: ImageVector,
    title: String,
    accent: Color,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(SECTION_INTERNAL_SPACING)) {
        HomeSectionTitle(
            icon = icon,
            title = title,
            accent = accent,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        content()
    }
}

@Composable
private fun HomeSectionTitle(
    icon: ImageVector,
    title: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(28.dp),
            shape = RoundedCornerShape(8.dp),
            color = accent.copy(alpha = 0.10f),
            border = BorderStroke(0.5.dp, accent.copy(alpha = 0.18f))
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = PureWhite
        )
    }
}

@Composable
private fun AlbumsSection(
    albums: List<AlbumSimple>,
    onOpenAlbum: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.height(200.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(albums, key = { _, album -> album.detailLookup }) { index, album ->
            Box(
                modifier = Modifier.clickable { onOpenAlbum(album.detailLookup) }
            ) {
                GridAlbumContent(album = album, rank = index + 1)
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
        modifier = modifier.height(168.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(artists, key = { _, artist -> artist.detailLookup }) { index, artist ->
            Box(
                modifier = Modifier.clickable { onOpenArtist(artist.detailLookup) }
            ) {
                CircleArtistContent(artist = artist, rank = index + 1)
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