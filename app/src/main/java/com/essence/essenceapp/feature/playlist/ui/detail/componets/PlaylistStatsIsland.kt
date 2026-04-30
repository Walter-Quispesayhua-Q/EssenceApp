package com.essence.essenceapp.feature.playlist.ui.detail.componets

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.playlist.domain.model.Playlist
import com.essence.essenceapp.feature.song.ui.components.GlassIsland
import com.essence.essenceapp.ui.theme.LuxeGold
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose
import java.time.format.DateTimeFormatter
import java.util.Locale

private val ShortDateFormatter = DateTimeFormatter.ofPattern("dd MMM", Locale("es", "ES"))

@Composable
internal fun PlaylistStatsIsland(
    playlist: Playlist,
    isSystem: Boolean,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(playlist.id) { visible = true }

    GlassIsland(
        modifier = modifier,
        accent = if (isSystem) SoftRose else MutedTeal,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(420)) +
                    scaleIn(initialScale = 0.94f, animationSpec = tween(420))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isSystem) {
                    StatCell(
                        icon = Icons.Default.Favorite,
                        value = "Likes",
                        label = "Origen",
                        tint = SoftRose
                    )
                } else {
                    StatCell(
                        icon = if (playlist.isPublic) Icons.Outlined.Public else Icons.Outlined.Lock,
                        value = if (playlist.isPublic) "Pública" else "Privada",
                        label = "Visibilidad",
                        tint = if (playlist.isPublic) MutedTeal else PureWhite.copy(alpha = 0.72f)
                    )
                }

                CellDivider()

                StatCell(
                    icon = Icons.Outlined.MusicNote,
                    value = "${playlist.totalSongs}",
                    label = "Canciones",
                    tint = SoftRose
                )

                if (playlist.isPublic && !isSystem) {
                    CellDivider()

                    StatCell(
                        icon = Icons.Default.Favorite,
                        value = "${playlist.totalLikes ?: 0L}",
                        label = "Likes",
                        tint = LuxeGold
                    )
                }

                CellDivider()

                StatCell(
                    icon = Icons.Outlined.CalendarMonth,
                    value = (playlist.updatedAt ?: playlist.createdAt).format(ShortDateFormatter),
                    label = "Actualizada",
                    tint = if (isSystem) SoftRose else MutedTeal
                )
            }
        }
    }
}

@Composable
private fun StatCell(
    icon: ImageVector,
    value: String,
    label: String,
    tint: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(18.dp)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = PureWhite,
            textAlign = TextAlign.Center
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = PureWhite.copy(alpha = 0.42f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CellDivider() {
    Box(
        modifier = Modifier
            .width(0.5.dp)
            .height(36.dp)
            .background(PureWhite.copy(alpha = 0.06f))
    )
}
