package com.essence.essenceapp.feature.playlist.ui.detail.componets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose

@Composable
internal fun PlaylistSongsHeader(
    totalSongs: Int,
    isSystem: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(SoftRose, SoftRose.copy(alpha = 0.30f))
                        )
                    )
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Canciones",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PureWhite
            )

            Spacer(modifier = Modifier.weight(1f))

            if (totalSongs > 0) {
                CountPill(count = totalSongs)
            }
        }

        if (isSystem) {
            Spacer(modifier = Modifier.height(8.dp))
            HelperText(
                text = "Para quitar una canción, deja de darle me gusta.",
                color = SoftRose.copy(alpha = 0.60f)
            )
        }
    }
}

@Composable
private fun CountPill(count: Int) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = SoftRose.copy(alpha = 0.10f),
        border = BorderStroke(1.dp, SoftRose.copy(alpha = 0.20f))
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
            text = "$count",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = SoftRose
        )
    }
}

@Composable
private fun HelperText(
    text: String,
    color: androidx.compose.ui.graphics.Color
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = color
    )
}
