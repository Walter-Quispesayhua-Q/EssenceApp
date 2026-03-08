package com.essence.essenceapp.shared.ui.components.cards.song

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.song.domain.model.SongSimple
import com.essence.essenceapp.shared.ui.components.cards.BaseCard
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import java.time.LocalDate

@Composable
fun CompactSongContent(
    song: SongSimple,
    modifier: Modifier = Modifier,
    durationText: String? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(45.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = song.artistName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            song.albumName?.takeIf { it.isNotBlank() }?.let { albumName ->
                Text(
                    text = albumName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        durationText?.let {
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private val previewSong = SongSimple(
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
)

@Preview(name = "Compact Song", showBackground = true)
@Composable
private fun PreviewCompactSongContent() {
    EssenceAppTheme {
        BaseCard {
            CompactSongContent(
                song = previewSong,
                durationText = "3:30"
            )
        }
    }
}