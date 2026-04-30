package com.essence.essenceapp.shared.ui.components.cards.artist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.essence.essenceapp.core.network.resolveImageUrl
import com.essence.essenceapp.feature.artist.domain.model.ArtistSimple
import com.essence.essenceapp.shared.ui.components.cards.BaseCard
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose

@Composable
fun CompactArtistContent(
    artist: ArtistSimple,
    modifier: Modifier = Modifier,
    showRoleLabel: Boolean = true
) {
    val imageUrl = resolveImageUrl(artist.imageKey)

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ArtistCompactAvatar(
            imageUrl = imageUrl,
            fallbackLetter = artist.nameArtist.take(1).uppercase(),
            contentDescription = artist.nameArtist
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = artist.nameArtist,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = PureWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (showRoleLabel) {
                Text(
                    text = "Artista",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = MutedTeal.copy(alpha = 0.65f)
                )
            }
        }
    }
}

@Composable
private fun ArtistCompactAvatar(
    imageUrl: String?,
    fallbackLetter: String,
    contentDescription: String?
) {
    Surface(
        modifier = Modifier.size(52.dp),
        shape = CircleShape,
        color = GraphiteSurface,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp
    ) {
        Box(modifier = Modifier.clip(CircleShape)) {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = contentDescription,
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
                                    MutedTeal.copy(alpha = 0.32f),
                                    SoftRose.copy(alpha = 0.22f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = fallbackLetter,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite.copy(alpha = 0.55f)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                PureWhite.copy(alpha = 0.16f),
                                PureWhite.copy(alpha = 0.04f)
                            )
                        ),
                        shape = CircleShape
                    )
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

private val previewArtistLong = ArtistSimple(
    id = 101L,
    nameArtist = "Daddy Yankee Featuring Natti Natasha",
    imageKey = null,
    artistUrl = "artists/daddy-yankee"
)

@Preview(name = "Compact Artist", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun Preview() {
    EssenceAppTheme {
        BaseCard {
            CompactArtistContent(artist = previewArtist)
        }
    }
}

@Preview(name = "Compact Artist - Long", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun LongPreview() {
    EssenceAppTheme {
        BaseCard {
            CompactArtistContent(artist = previewArtistLong)
        }
    }
}