package com.essence.essenceapp.feature.playlist.ui.detail.componets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.essence.essenceapp.feature.playlist.domain.PlaylistUtils
import com.essence.essenceapp.feature.playlist.domain.model.Playlist
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose

@Composable
internal fun PlaylistHeroInfoIsland(
    playlist: Playlist,
    isSystem: Boolean,
    isOwner: Boolean,
    canPlay: Boolean,
    onPlay: () -> Unit,
    onShuffle: () -> Unit,
    onEdit: () -> Unit,
    onAddSongs: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayTitle = PlaylistUtils.getDisplayTitle(playlist.type, playlist.title)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (isSystem) {
            SystemBadge()
        } else {
            VisibilityBadge(isPublic = playlist.isPublic)
        }

        Text(
            text = displayTitle,
            style = MaterialTheme.typography.displaySmall.copy(
                letterSpacing = (-0.4).sp
            ),
            fontWeight = FontWeight.Bold,
            color = PureWhite,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        if (!isSystem && !playlist.description.isNullOrBlank()) {
            Text(
                text = playlist.description,
                style = MaterialTheme.typography.bodyMedium,
                color = PureWhite.copy(alpha = 0.72f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }

        if (isSystem) {
            Text(
                text = "Las canciones a las que diste me gusta aparecen aquí automáticamente.",
                style = MaterialTheme.typography.bodySmall,
                color = PureWhite.copy(alpha = 0.52f),
                textAlign = TextAlign.Center
            )
        }

        PlaylistDetailActionsRow(
            showEditAndAdd = !isSystem && isOwner,
            enabled = canPlay,
            onPlay = onPlay,
            onShuffle = onShuffle,
            onEdit = onEdit,
            onAddSongs = onAddSongs,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

@Composable
private fun SystemBadge() {
    BadgePill(
        icon = Icons.Default.AutoAwesome,
        text = "Colección automática",
        accent = SoftRose
    )
}

@Composable
private fun VisibilityBadge(isPublic: Boolean) {
    val accent = if (isPublic) MutedTeal else PureWhite.copy(alpha = 0.72f)
    val icon = if (isPublic) Icons.Outlined.Public else Icons.Outlined.Lock
    val text = if (isPublic) "Playlist pública" else "Playlist privada"

    BadgePill(icon = icon, text = text, accent = accent)
}

@Composable
private fun BadgePill(
    icon: ImageVector,
    text: String,
    accent: Color
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = accent.copy(alpha = 0.10f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.20f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(13.dp)
            )

            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = accent
            )
        }
    }
}
