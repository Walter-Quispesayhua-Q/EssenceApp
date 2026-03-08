package com.essence.essenceapp.shared.ui.components.cards.artist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.artist.domain.model.ArtistSimple
import com.essence.essenceapp.shared.ui.components.cards.BaseCard

@Composable
fun CircleArtistContent(
    artist: ArtistSimple,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(120.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiary,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = artist.nameArtist,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

private val previewArtist = ArtistSimple(
    id = 100L,
    nameArtist = "Bad Bunny",
    imageKey = null,
    artistUrl = "artists/bad-bunny"
)

@Preview(name = "Circle Artist", showBackground = true)
@Composable
private fun PreviewCircleArtistContent() {
    MaterialTheme {
        BaseCard {
            CircleArtistContent(artist = previewArtist)
        }
    }
}