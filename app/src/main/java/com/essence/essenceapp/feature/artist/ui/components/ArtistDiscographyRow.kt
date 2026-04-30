package com.essence.essenceapp.feature.artist.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.essence.essenceapp.core.network.resolveImageUrl
import com.essence.essenceapp.feature.album.domain.model.AlbumSimple
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.LuxeGold
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose

@Composable
internal fun ArtistDiscographyRow(
    albums: List<AlbumSimple>,
    onOpenAlbum: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(albums, key = { it.detailLookup }) { album ->
            DiscographyAlbumCard(
                album = album,
                onClick = { onOpenAlbum(album.detailLookup) }
            )
        }
    }
}

@Composable
private fun DiscographyAlbumCard(
    album: AlbumSimple,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(156.dp)
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier.size(156.dp),
            contentAlignment = Alignment.Center
        ) {
            CardHalo()

            Surface(
                modifier = Modifier.size(148.dp),
                shape = RoundedCornerShape(18.dp),
                color = GraphiteSurface.copy(alpha = 0.85f),
                border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.08f))
            ) {
                AlbumCoverImage(album = album)
            }
        }

        Column(
            modifier = Modifier.padding(horizontal = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = album.title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = PureWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            album.release?.year?.let { year ->
                YearTypeRow(year = year)
            }
        }
    }
}

@Composable
private fun CardHalo() {
    Box(
        modifier = Modifier
            .size(156.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        LuxeGold.copy(alpha = 0.18f),
                        MutedTeal.copy(alpha = 0.08f),
                        Color.Transparent
                    )
                )
            )
    )
}

@Composable
private fun AlbumCoverImage(album: AlbumSimple) {
    val imageUrl = resolveImageUrl(album.imageKey)
    if (imageUrl != null) {
        AsyncImage(
            model = imageUrl,
            contentDescription = album.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            LuxeGold.copy(alpha = 0.30f),
                            SoftRose.copy(alpha = 0.18f),
                            GraphiteSurface
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Album,
                contentDescription = null,
                tint = PureWhite.copy(alpha = 0.32f),
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
private fun YearTypeRow(year: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "Album",
            style = MaterialTheme.typography.labelSmall,
            color = LuxeGold.copy(alpha = 0.82f),
            fontWeight = FontWeight.Medium
        )
        Box(
            modifier = Modifier
                .size(2.dp)
                .background(PureWhite.copy(alpha = 0.30f))
        )
        Text(
            text = year.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = PureWhite.copy(alpha = 0.55f)
        )
    }
}
