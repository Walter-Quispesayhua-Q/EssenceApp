package com.essence.essenceapp.shared.ui.components.cards.artist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.artist.domain.model.ArtistSimple
import com.essence.essenceapp.shared.ui.components.cards.BaseCard
import com.essence.essenceapp.ui.theme.EssenceAppTheme

@Composable
fun CompactArtistContent(
    artist: ArtistSimple,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(45.dp)
                .background(
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiary
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = artist.nameArtist,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private val previewArtist = ArtistSimple(
    id = 100L,
    nameArtist = "Bad Bunny",
    imageKey = null,
    artistUrl = "artists/bad-bunny"
)

@Preview(name = "Compact Artist", showBackground = true)
@Composable
private fun PreviewCompactArtistContent() {
    EssenceAppTheme {
        BaseCard {
            CompactArtistContent(artist = previewArtist)
        }
    }
}