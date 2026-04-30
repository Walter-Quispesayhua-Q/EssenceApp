package com.essence.essenceapp.feature.artist.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.artist.domain.model.Artist
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite

@Composable
internal fun ArtistHeroInfoIsland(
    artist: Artist,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        VerifiedArtistBadge()

        Text(
            text = artist.nameArtist,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = PureWhite,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        artist.country?.takeIf { it.isNotBlank() }?.let { country ->
            CountryChip(country = country)
        }

        artist.description?.takeIf { it.isNotBlank() }?.let { description ->
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = PureWhite.copy(alpha = 0.62f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}

@Composable
private fun VerifiedArtistBadge() {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = MutedTeal.copy(alpha = 0.16f),
        border = BorderStroke(1.dp, MutedTeal.copy(alpha = 0.36f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MutedTeal,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "Artista verificado",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = PureWhite.copy(alpha = 0.92f)
            )
        }
    }
}

@Composable
private fun CountryChip(country: String) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = PureWhite.copy(alpha = 0.06f),
        border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.12f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Public,
                contentDescription = null,
                tint = PureWhite.copy(alpha = 0.72f),
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = country,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = PureWhite.copy(alpha = 0.82f)
            )
        }
    }
}
