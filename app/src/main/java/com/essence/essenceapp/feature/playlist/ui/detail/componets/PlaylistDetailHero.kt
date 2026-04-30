package com.essence.essenceapp.feature.playlist.ui.detail.componets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.essence.essenceapp.core.network.resolveImageUrl
import com.essence.essenceapp.feature.playlist.domain.model.Playlist
import com.essence.essenceapp.ui.theme.MidnightBlack
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.SoftRose

@Composable
internal fun PlaylistDetailHero(
    playlist: Playlist,
    isSystem: Boolean,
    isOwner: Boolean,
    isLikeSubmitting: Boolean,
    canPlay: Boolean,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onToggleLike: () -> Unit,
    onPlay: () -> Unit,
    onShuffle: () -> Unit,
    onEdit: () -> Unit,
    onAddSongs: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusBarTopPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MidnightBlack)
    ) {
        HeroBackdrop(
            playlist = playlist,
            isSystem = isSystem
        )

        HeroFadeOverlay()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = statusBarTopPadding + 12.dp,
                    bottom = 22.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            HeroTopBar(
                isSystem = isSystem,
                isOwner = isOwner,
                isPublic = playlist.isPublic,
                isLiked = playlist.isLiked,
                isLikeSubmitting = isLikeSubmitting,
                onBack = onBack,
                onDelete = onDelete,
                onToggleLike = onToggleLike
            )

            PlaylistCoverArt(
                playlist = playlist,
                isSystem = isSystem
            )

            PlaylistHeroInfoIsland(
                playlist = playlist,
                isSystem = isSystem,
                isOwner = isOwner,
                canPlay = canPlay,
                onPlay = onPlay,
                onShuffle = onShuffle,
                onEdit = onEdit,
                onAddSongs = onAddSongs
            )
        }
    }
}

@Composable
private fun BoxScope.HeroBackdrop(
    playlist: Playlist,
    isSystem: Boolean
) {
    val imageUrl = resolveImageUrl(playlist.imageKey)

    Box(modifier = Modifier.matchParentSize()) {
        if (isSystem) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                SoftRose.copy(alpha = 0.30f),
                                Color(0xFFBB4477).copy(alpha = 0.18f),
                                MidnightBlack.copy(alpha = 0.95f)
                            )
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                SoftRose.copy(alpha = 0.14f),
                                Color.Transparent
                            ),
                            radius = 950f
                        )
                    )
            )
        } else if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = playlist.title,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MutedTeal.copy(alpha = 0.28f),
                                SoftRose.copy(alpha = 0.20f),
                                MidnightBlack.copy(alpha = 0.95f)
                            )
                        )
                    )
            )
        }
    }
}

@Composable
private fun BoxScope.HeroFadeOverlay() {
    Box(
        modifier = Modifier
            .matchParentSize()
            .background(
                Brush.verticalGradient(
                    0.00f to MidnightBlack.copy(alpha = 0.20f),
                    0.18f to Color.Transparent,
                    0.45f to Color.Transparent,
                    0.65f to MidnightBlack.copy(alpha = 0.42f),
                    0.85f to MidnightBlack.copy(alpha = 0.82f),
                    1.00f to MidnightBlack
                )
            )
    )
}

@Composable
private fun HeroTopBar(
    isSystem: Boolean,
    isOwner: Boolean,
    isPublic: Boolean,
    isLiked: Boolean,
    isLikeSubmitting: Boolean,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onToggleLike: () -> Unit
) {
    val showLike = !isSystem && isPublic && !isOwner
    val showDelete = !isSystem && isOwner

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FloatingCircleButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Volver",
            onClick = onBack
        )

        if (showLike || showDelete) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showLike) {
                    LikeButton(
                        isLiked = isLiked,
                        isSubmitting = isLikeSubmitting,
                        onClick = onToggleLike
                    )
                }

                if (showDelete) {
                    FloatingCircleButton(
                        icon = Icons.Default.Delete,
                        contentDescription = "Eliminar playlist",
                        onClick = onDelete
                    )
                }
            }
        }
    }
}
