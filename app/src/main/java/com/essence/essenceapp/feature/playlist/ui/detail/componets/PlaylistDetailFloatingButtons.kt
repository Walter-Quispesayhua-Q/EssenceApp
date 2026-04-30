package com.essence.essenceapp.feature.playlist.ui.detail.componets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose

@Composable
internal fun FloatingCircleButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = PureWhite,
) {
    Surface(
        modifier = modifier.size(44.dp),
        shape = CircleShape,
        color = GraphiteSurface.copy(alpha = 0.78f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.10f)),
        shadowElevation = 6.dp
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = accent.copy(alpha = 0.92f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
internal fun LikeButton(
    isLiked: Boolean,
    isSubmitting: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.size(44.dp),
        shape = CircleShape,
        color = GraphiteSurface.copy(alpha = 0.85f),
        border = BorderStroke(
            1.dp,
            if (isLiked) SoftRose.copy(alpha = 0.32f)
            else PureWhite.copy(alpha = 0.10f)
        ),
        shadowElevation = 6.dp
    ) {
        IconButton(onClick = onClick, enabled = !isSubmitting) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = SoftRose
                )
            } else {
                Icon(
                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isLiked) "Quitar like" else "Dar like",
                    tint = if (isLiked) SoftRose else PureWhite.copy(alpha = 0.62f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
