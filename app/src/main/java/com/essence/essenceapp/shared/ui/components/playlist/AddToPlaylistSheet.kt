package com.essence.essenceapp.shared.ui.components.playlist

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.essence.essenceapp.feature.playlist.domain.PlaylistUtils
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToPlaylistSheet(
    songKey: String,
    onDismiss: () -> Unit,
    viewModel: AddToPlaylistViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(songKey) {
        viewModel.prepare(songKey)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Agregar a playlist",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Elige una de tus playlists para guardar esta cancion.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.58f)
            )

            if (state.error != null && state.playlists.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                ErrorBanner(message = state.error ?: "Ocurrio un error")
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp,
                            color = MutedTeal
                        )
                    }
                }

                state.error != null && state.playlists.isEmpty() -> {
                    ErrorBanner(message = state.error ?: "Ocurrio un error")
                }

                state.playlists.isEmpty() -> {
                    EmptyPlaylistsState()
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        items(state.playlists, key = { it.playlist.id }) { entry ->
                            PlaylistItem(
                                entry = entry,
                                isAdding = state.addingToPlaylistId == entry.playlist.id,
                                isAdded = state.addedToPlaylistId == entry.playlist.id,
                                onClick = {
                                    if (
                                        state.addingToPlaylistId == null &&
                                        state.addedToPlaylistId != entry.playlist.id &&
                                        !entry.alreadyContainsSong
                                    ) {
                                        viewModel.addSongToPlaylist(entry.playlist.id, songKey)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaylistItem(
    entry: PlaylistEntry,
    isAdding: Boolean,
    isAdded: Boolean,
    onClick: () -> Unit
) {
    val playlist = entry.playlist
    val isLiked = PlaylistUtils.isSystemPlaylist(playlist.type)
    val displayTitle = PlaylistUtils.getDisplayTitle(playlist.type, playlist.title)
    val alreadyContains = entry.alreadyContainsSong
    val showAsDone = isAdded || alreadyContains
    val isInteractable = !isAdding && !showAsDone

    val accentColor = if (isLiked) SoftRose else MutedTeal
    val leadingIcon = if (isLiked) Icons.Default.Favorite else Icons.Default.PlaylistPlay

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isInteractable, onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = GraphiteSurface.copy(alpha = if (showAsDone) 0.40f else 0.58f),
        border = BorderStroke(
            1.dp,
            when {
                showAsDone -> MutedTeal.copy(alpha = 0.24f)
                isLiked -> SoftRose.copy(alpha = 0.16f)
                else -> PureWhite.copy(alpha = 0.06f)
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(14.dp),
                color = accentColor.copy(alpha = 0.12f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = displayTitle,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = if (showAsDone) 0.66f else 1f
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (!isLiked) {
                        SheetPill(
                            icon = if (playlist.isPublic) Icons.Outlined.Public else Icons.Outlined.Lock,
                            text = if (playlist.isPublic) "Publica" else "Privada",
                            accent = if (playlist.isPublic) MutedTeal else PureWhite.copy(alpha = 0.72f)
                        )
                    }

                    if (alreadyContains) {
                        SheetPill(
                            icon = Icons.Default.Check,
                            text = "Ya agregada",
                            accent = MutedTeal
                        )
                    }

                    playlist.totalLikes?.takeIf { it > 0 }?.let { likes ->
                        SheetPill(
                            icon = Icons.Default.Favorite,
                            text = formatLikes(likes),
                            accent = SoftRose
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            AnimatedContent(
                targetState = when {
                    isAdding -> "loading"
                    showAsDone -> "done"
                    else -> "idle"
                },
                transitionSpec = {
                    (scaleIn() + fadeIn()) togetherWith (scaleOut() + fadeOut())
                },
                label = "playlist_add_state"
            ) { current ->
                when (current) {
                    "loading" -> CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = SoftRose
                    )

                    "done" -> Surface(
                        modifier = Modifier.size(30.dp),
                        shape = CircleShape,
                        color = MutedTeal.copy(alpha = 0.14f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Agregada",
                                tint = MutedTeal,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    else -> Unit
                }
            }
        }
    }
}

@Composable
private fun SheetPill(
    icon: ImageVector,
    text: String,
    accent: Color
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = accent.copy(alpha = 0.08f),
        border = BorderStroke(0.5.dp, accent.copy(alpha = 0.14f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent.copy(alpha = 0.88f),
                modifier = Modifier.size(11.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = accent.copy(alpha = 0.88f)
            )
        }
    }
}

@Composable
private fun ErrorBanner(message: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.error.copy(alpha = 0.08f),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.18f))
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun EmptyPlaylistsState() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(20.dp),
        color = GraphiteSurface.copy(alpha = 0.45f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlaylistPlay,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = "No tienes playlists aun",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Crea una playlist y vuelve a intentarlo.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

private fun formatLikes(value: Long): String {
    return when {
        value >= 1_000_000 -> String.format(java.util.Locale.US, "%.1fM", value / 1_000_000f)
        value >= 1_000 -> String.format(java.util.Locale.US, "%.1fK", value / 1_000f)
        else -> value.toString()
    }
}