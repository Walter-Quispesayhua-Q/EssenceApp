package com.essence.essenceapp.feature.album.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.essence.essenceapp.core.network.resolveImageUrl
import com.essence.essenceapp.feature.album.domain.model.Album
import com.essence.essenceapp.feature.song.domain.model.SongSimple
import com.essence.essenceapp.ui.theme.MidnightBlack
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.SoftRose

@Composable
internal fun AlbumDetailHero(
    album: Album,
    songs: List<SongSimple>,
    isLikeSubmitting: Boolean,
    onBack: () -> Unit,
    onToggleLike: () -> Unit,
    modifier: Modifier = Modifier
) {
    val artistText = remember(album, songs) { resolveArtistText(album, songs) }
    val statusBarTopPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MidnightBlack)
    ) {
        HeroBackdrop(imageKey = album.imageKey, contentDescription = album.title)
        HeroFadeOverlay()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = statusBarTopPadding + 12.dp,
                    bottom = 18.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            AlbumHeroTopBar(
                isLiked = album.isLiked,
                isLikeSubmitting = isLikeSubmitting,
                onBack = onBack,
                onToggleLike = onToggleLike
            )

            AlbumCoverArt(
                imageKey = album.imageKey,
                title = album.title
            )

            AlbumHeroInfoIsland(
                album = album,
                artistText = artistText
            )
        }
    }
}

@Composable
private fun BoxScope.HeroBackdrop(imageKey: String?, contentDescription: String) {
    val imageUrl = resolveImageUrl(imageKey)
    if (imageUrl != null) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = contentDescription,
            modifier = Modifier
                .matchParentSize()
                .blur(38.dp),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            SoftRose.copy(alpha = 0.18f),
                            MutedTeal.copy(alpha = 0.12f),
                            MidnightBlack
                        )
                    )
                )
        )
    }
}

@Composable
private fun BoxScope.HeroFadeOverlay() {
    Box(
        modifier = Modifier
            .matchParentSize()
            .background(
                Brush.verticalGradient(
                    0.00f to MidnightBlack.copy(alpha = 0.50f),
                    0.18f to MidnightBlack.copy(alpha = 0.28f),
                    0.42f to MidnightBlack.copy(alpha = 0.32f),
                    0.68f to MidnightBlack.copy(alpha = 0.62f),
                    0.88f to MidnightBlack.copy(alpha = 0.88f),
                    1.00f to MidnightBlack
                )
            )
    )
}

private fun resolveArtistText(album: Album, songs: List<SongSimple>): String {
    return when {
        album.artists.isNotEmpty() -> album.artists.joinToString(", ") { it.nameArtist }
        songs.isNotEmpty() -> songs.first().artistName
        else -> "Album oficial"
    }
}
