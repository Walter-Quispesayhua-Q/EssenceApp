package com.essence.essenceapp.shared.ui.components.cards.album

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.essence.essenceapp.core.network.resolveImageUrl
import com.essence.essenceapp.feature.album.domain.model.AlbumSimple
import com.essence.essenceapp.shared.ui.components.badges.RankBadge
import com.essence.essenceapp.shared.ui.components.cards.BaseCard
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.LuxeGold
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose
import java.time.LocalDate

private val GridCoverShape: Shape = RoundedCornerShape(20.dp)

@Composable
fun GridAlbumContent(
    album: AlbumSimple,
    modifier: Modifier = Modifier,
    rank: Int? = null
) {
    val imageUrl = resolveImageUrl(album.imageKey)
    val artistsText = album.artists
        ?.filter { it.isNotBlank() }
        ?.joinToString(", ")
        .orEmpty()

    Column(
        modifier = modifier.width(130.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AlbumGridCover(
            imageUrl = imageUrl,
            fallbackLetters = album.title.take(2).uppercase(),
            contentDescription = album.title,
            rank = rank
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = album.title,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = PureWhite,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        if (artistsText.isNotBlank()) {
            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = artistsText,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = PureWhite.copy(alpha = 0.55f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }

        album.release?.let { date ->
            Spacer(modifier = Modifier.height(6.dp))
            YearPill(year = "${date.year}")
        }
    }
}

@Composable
private fun AlbumGridCover(
    imageUrl: String?,
    fallbackLetters: String,
    contentDescription: String?,
    rank: Int? = null
) {
    Box(modifier = Modifier.size(100.dp)) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = GridCoverShape,
            color = GraphiteSurface,
            shadowElevation = 0.dp,
            tonalElevation = 0.dp
        ) {
            Box(modifier = Modifier.clip(GridCoverShape)) {
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
                                        LuxeGold.copy(alpha = 0.32f),
                                        MutedTeal.copy(alpha = 0.22f),
                                        SoftRose.copy(alpha = 0.16f)
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
                            shape = GridCoverShape
                        )
                )
            }
        }

        if (rank != null) {
            RankBadge(
                position = rank,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp)
            )
        }
    }
}

@Composable
private fun YearPill(year: String) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = Color.Transparent,
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            LuxeGold.copy(alpha = 0.14f),
                            LuxeGold.copy(alpha = 0.06f)
                        )
                    )
                )
                .border(
                    width = 0.5.dp,
                    color = LuxeGold.copy(alpha = 0.22f),
                    shape = RoundedCornerShape(999.dp)
                )
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = year,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = LuxeGold.copy(alpha = 0.9f),
                maxLines = 1
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

private val previewAlbumMinimal = AlbumSimple(
    id = 11L,
    title = "YHLQMDLG",
    imageKey = null,
    albumUrl = "albums/yhlqmdlg",
    artists = null,
    release = null
)

@Preview(name = "Grid Album - Full", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun FullPreview() {
    EssenceAppTheme {
        BaseCard {
            GridAlbumContent(album = previewAlbum)
        }
    }
}

@Preview(name = "Grid Album - Minimal", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun MinimalPreview() {
    EssenceAppTheme {
        BaseCard {
            GridAlbumContent(album = previewAlbumMinimal)
        }
    }
}