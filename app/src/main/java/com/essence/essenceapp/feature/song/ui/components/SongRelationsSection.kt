package com.essence.essenceapp.feature.song.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.album.domain.model.AlbumSimple
import com.essence.essenceapp.feature.artist.domain.model.ArtistSimple
import com.essence.essenceapp.shared.ui.components.cards.album.CompactAlbumContent
import com.essence.essenceapp.shared.ui.components.cards.artist.CompactArtistContent
import com.essence.essenceapp.ui.theme.LuxeGold
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite

@Composable
internal fun AlbumIsland(
    album: AlbumSimple,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassIsland(
        accent = LuxeGold,
        isPulsing = false,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            SectionLabel(
                text = "Album",
                accent = LuxeGold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(onClick = onClick)
                    .padding(horizontal = 2.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    CompactAlbumContent(album = album)
                }
                ChevronForward(accent = LuxeGold)
            }
        }
    }
}

@Composable
internal fun ArtistsIsland(
    artists: List<ArtistSimple>,
    onArtistClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (artists.isEmpty()) return

    val label = if (artists.size == 1) "Artista" else "Artistas"

    GlassIsland(
        accent = MutedTeal,
        isPulsing = false,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            SectionLabel(
                text = label,
                accent = MutedTeal
            )

            Spacer(modifier = Modifier.height(8.dp))

            artists.forEachIndexed { index, artist ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onArtistClick(artist.detailLookup) }
                        .padding(horizontal = 2.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        CompactArtistContent(artist = artist)
                    }
                    ChevronForward(accent = MutedTeal)
                }

                if (index < artists.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 56.dp)
                            .height(1.dp)
                            .clip(RoundedCornerShape(1.dp))
                            .background(PureWhite.copy(alpha = 0.06f))
                    )
                }
            }
        }
    }
}

@Composable
internal fun CombinedInfoIsland(
    album: AlbumSimple?,
    artists: List<ArtistSimple>,
    onAlbumClick: (String) -> Unit,
    onArtistClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (album == null && artists.isEmpty()) return

    GlassIsland(
        accent = LuxeGold,
        isPulsing = false,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (album != null) {
                SectionLabel(text = "Album", accent = LuxeGold)
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onAlbumClick(album.detailLookup) }
                        .padding(horizontal = 2.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        CompactAlbumContent(album = album)
                    }
                    ChevronForward(accent = LuxeGold)
                }
            }

            if (album != null && artists.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(PureWhite.copy(alpha = 0.08f))
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (artists.isNotEmpty()) {
                val label = if (artists.size == 1) "Artista" else "Artistas"
                SectionLabel(text = label, accent = MutedTeal)
                Spacer(modifier = Modifier.height(6.dp))

                artists.forEachIndexed { index, artist ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onArtistClick(artist.detailLookup) }
                            .padding(horizontal = 2.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            CompactArtistContent(artist = artist)
                        }
                        ChevronForward(accent = MutedTeal)
                    }

                    if (index < artists.lastIndex) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 56.dp)
                                .height(1.dp)
                                .clip(RoundedCornerShape(1.dp))
                                .background(PureWhite.copy(alpha = 0.06f))
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(
    text: String,
    accent: Color
) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        color = accent.copy(alpha = 0.82f),
        modifier = Modifier.padding(horizontal = 2.dp)
    )
}

@Composable
private fun ChevronForward(accent: Color) {
    Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = null,
        tint = accent.copy(alpha = 0.70f),
        modifier = Modifier
            .size(24.dp)
            .padding(start = 4.dp)
    )
}
