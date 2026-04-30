package com.essence.essenceapp.feature.playlist.ui.detail.componets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.MidnightBlack
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose

@Composable
internal fun PlaylistDetailActionsRow(
    showEditAndAdd: Boolean,
    enabled: Boolean,
    onPlay: () -> Unit,
    onShuffle: () -> Unit,
    onEdit: () -> Unit,
    onAddSongs: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        PlayPrimaryButton(
            modifier = Modifier.weight(1f),
            enabled = enabled,
            onClick = onPlay
        )

        ShuffleSecondaryButton(
            enabled = enabled,
            onClick = onShuffle
        )

        if (showEditAndAdd) {
            CircleIconAction(
                icon = Icons.Outlined.Edit,
                contentDescription = "Editar playlist",
                accent = MutedTeal,
                onClick = onEdit
            )

            CircleIconAction(
                icon = Icons.Default.PlaylistAdd,
                contentDescription = "Agregar canciones",
                accent = SoftRose,
                onClick = onAddSongs
            )
        }
    }
}

@Composable
private fun PlayPrimaryButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val alpha = if (enabled) 1f else 0.45f

    Surface(
        modifier = modifier
            .height(50.dp)
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(999.dp),
        color = Color.Transparent,
        shadowElevation = if (enabled) 8.dp else 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            SoftRose.copy(alpha = 0.95f * alpha),
                            Color(0xFFBB4477).copy(alpha = 0.92f * alpha)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = MidnightBlack.copy(alpha = 0.92f),
                    modifier = Modifier.size(22.dp)
                )

                Text(
                    text = "Reproducir",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MidnightBlack.copy(alpha = 0.92f)
                )
            }
        }
    }
}

@Composable
private fun ShuffleSecondaryButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    val alpha = if (enabled) 1f else 0.45f

    Surface(
        modifier = Modifier
            .size(50.dp)
            .clickable(enabled = enabled, onClick = onClick),
        shape = CircleShape,
        color = GraphiteSurface.copy(alpha = 0.78f),
        border = BorderStroke(1.dp, MutedTeal.copy(alpha = 0.40f * alpha))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Shuffle,
                contentDescription = "Aleatorio",
                tint = MutedTeal.copy(alpha = alpha),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun CircleIconAction(
    icon: ImageVector,
    contentDescription: String,
    accent: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.size(44.dp),
        shape = CircleShape,
        color = GraphiteSurface.copy(alpha = 0.62f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.20f))
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = accent.copy(alpha = 0.88f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
