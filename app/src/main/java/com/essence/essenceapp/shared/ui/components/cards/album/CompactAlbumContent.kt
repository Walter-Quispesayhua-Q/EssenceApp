package com.essence.essenceapp.shared.ui.components.cards.album

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
import androidx.compose.material.icons.filled.Album
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.album.domain.model.AlbumSimple
import com.essence.essenceapp.shared.ui.components.cards.BaseCard
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import java.time.LocalDate

@Composable
fun CompactAlbumContent(
    album: AlbumSimple,
    modifier: Modifier = Modifier,
    releaseText: String? = null
) {
    val artistsText = album.artists
        ?.filter { it.isNotBlank() }
        ?.joinToString(", ")
        .orEmpty()

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(45.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Album,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondary
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = album.title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (artistsText.isNotBlank()) {
                Text(
                    text = artistsText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        releaseText?.let {
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private val previewAlbum = AlbumSimple(
    id = 10L,
    title = "Un Verano Sin Ti",
    imageKey = null,
    albumUrl = "albums/un-verano-sin-ti",
    artists = listOf("Bad Bunny"),
    release = LocalDate.of(2022, 5, 6)
)

@Preview(name = "Compact Album", showBackground = true)
@Composable
private fun PreviewCompactAlbumContent() {
    EssenceAppTheme {
        BaseCard {
            CompactAlbumContent(
                album = previewAlbum,
                releaseText = "2022"
            )
        }
    }
}