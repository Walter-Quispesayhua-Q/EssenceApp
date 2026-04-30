package com.essence.essenceapp.shared.ui.components.cards.album

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.essence.essenceapp.core.network.resolveImageUrl
import com.essence.essenceapp.feature.album.domain.model.AlbumSimple
import com.essence.essenceapp.shared.ui.components.cards.BaseCard
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.LuxeGold
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose
import java.time.LocalDate

private val CompactCoverShape: Shape = RoundedCornerShape(16.dp)

@Composable
fun CompactAlbumContent(
    album: AlbumSimple,
    modifier: Modifier = Modifier
) {
    val imageUrl = resolveImageUrl(album.imageKey)
    val artistsText = album.artists
        ?.filter { it.isNotBlank() }
        ?.joinToString(", ")
        .orEmpty()

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AlbumCompactCover(
            imageUrl = imageUrl,
            fallbackLetter = album.title.take(1).uppercase(),
            contentDescription = album.title
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = album.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = PureWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (artistsText.isNotBlank()) {
                Text(
                    text = artistsText,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = PureWhite.copy(alpha = 0.65f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        album.release?.let { date ->
            YearPill(year = "${date.year}")
        }
    }
}

@Composable
private fun AlbumCompactCover(
    imageUrl: String?,
    fallbackLetter: String,
    contentDescription: String?
) {
    Surface(
        modifier = Modifier.size(52.dp),
        shape = CompactCoverShape,
        color = GraphiteSurface,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp
    ) {
        Box(modifier = Modifier.clip(CompactCoverShape)) {
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
                                    SoftRose.copy(alpha = 0.18f)
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
                        shape = CompactCoverShape
                    )
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

@Preview(name = "Compact Album - Full", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun FullPreview() {
    EssenceAppTheme {
        BaseCard {
            CompactAlbumContent(album = previewAlbum)
        }
    }
}

@Preview(name = "Compact Album - Minimal", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun MinimalPreview() {
    EssenceAppTheme {
        BaseCard {
            CompactAlbumContent(album = previewAlbumMinimal)
        }
    }
}