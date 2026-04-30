package com.essence.essenceapp.shared.ui.components.cards.artist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.essence.essenceapp.core.network.resolveImageUrl
import com.essence.essenceapp.feature.artist.domain.model.ArtistSimple
import com.essence.essenceapp.shared.ui.components.badges.RankBadge
import com.essence.essenceapp.shared.ui.components.cards.BaseCard
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose

@Composable
fun CircleArtistContent(
    artist: ArtistSimple,
    modifier: Modifier = Modifier,
    showRoleLabel: Boolean = true,
    rank: Int? = null
) {
    val imageUrl = resolveImageUrl(artist.imageKey)

    Column(
        modifier = modifier.width(130.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ArtistCircleAvatar(
            imageUrl = imageUrl,
            fallbackLetters = artist.nameArtist.take(2).uppercase(),
            contentDescription = artist.nameArtist,
            rank = rank
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = artist.nameArtist,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = PureWhite,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        if (showRoleLabel) {
            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Artista",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = MutedTeal.copy(alpha = 0.65f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ArtistCircleAvatar(
    imageUrl: String?,
    fallbackLetters: String,
    contentDescription: String?,
    rank: Int? = null
) {
    Box(modifier = Modifier.size(100.dp)) {
        Surface(
            modifier = Modifier.fillMaxSize(),
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
                            text = fallbackLetters,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite.copy(alpha = 0.4f)
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

        if (rank != null) {
            RankBadge(
                position = rank,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(2.dp)
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
    nameArtist = "Daddy Yankee",
    imageKey = null,
    artistUrl = "artists/daddy-yankee"
)

@Preview(name = "Circle Artist", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun Preview() {
    EssenceAppTheme {
        BaseCard {
            CircleArtistContent(artist = previewArtist)
        }
    }
}

@Preview(name = "Circle Artist - Long", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun LongPreview() {
    EssenceAppTheme {
        BaseCard {
            CircleArtistContent(artist = previewArtistLong)
        }
    }
}