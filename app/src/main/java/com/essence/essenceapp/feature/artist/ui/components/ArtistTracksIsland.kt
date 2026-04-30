package com.essence.essenceapp.feature.artist.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.song.domain.model.SongSimple
import com.essence.essenceapp.feature.song.ui.components.GlassIsland
import com.essence.essenceapp.shared.ui.components.cards.song.CompactSongContent
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose

@Composable
internal fun ArtistTracksIsland(
    songs: List<SongSimple>,
    onOpenSong: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    GlassIsland(
        modifier = modifier,
        accent = SoftRose,
        contentPadding = PaddingValues(vertical = 6.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            songs.forEachIndexed { index, song ->
                TrackRow(
                    index = index + 1,
                    isFirst = index == 0,
                    song = song,
                    onClick = { onOpenSong(song.detailLookup) }
                )
                if (index < songs.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 56.dp, end = 16.dp),
                        thickness = 0.5.dp,
                        color = PureWhite.copy(alpha = 0.05f)
                    )
                }
            }
        }
    }
}

@Composable
private fun TrackRow(
    index: Int,
    isFirst: Boolean,
    song: SongSimple,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = index.toString().padStart(2, '0'),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = if (isFirst) MutedTeal else PureWhite.copy(alpha = 0.40f),
            modifier = Modifier.width(28.dp)
        )

        Box(modifier = Modifier.weight(1f)) {
            CompactSongContent(
                song = song,
                durationText = formatTrackDuration(song.durationMs),
                showAddToPlaylistAction = true
            )
        }
    }
}
