package com.essence.essenceapp.feature.playlist.ui.list.componets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.playlist.domain.model.PlaylistSimple
import com.essence.essenceapp.shared.ui.components.cards.playlist.CompactPlaylistContent
import com.essence.essenceapp.ui.theme.MutedTeal

private const val PREVIEW_COUNT = 3

@Composable
internal fun PublicPlaylistsIsland(
    playlists: List<PlaylistSimple>,
    onOpen: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    val hasMore = playlists.size > PREVIEW_COUNT

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            playlists.take(PREVIEW_COUNT).forEachIndexed { index, playlist ->
                PublicPlaylistRow(playlist = playlist, onOpen = onOpen)
                if (index < minOf(PREVIEW_COUNT, playlists.size) - 1 || isExpanded) {
                    IslandDivider()
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    playlists.drop(PREVIEW_COUNT).forEachIndexed { index, playlist ->
                        PublicPlaylistRow(playlist = playlist, onOpen = onOpen)
                        if (index < playlists.size - PREVIEW_COUNT - 1) {
                            IslandDivider()
                        }
                    }
                }
            }

            if (hasMore) {
                IslandDivider()
                ExpandToggleButton(
                    isExpanded = isExpanded,
                    totalCount = playlists.size,
                    onToggle = { isExpanded = !isExpanded }
                )
            }
        }
    }
}

@Composable
private fun PublicPlaylistRow(
    playlist: PlaylistSimple,
    onOpen: (Long) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen(playlist.id) }
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        CompactPlaylistContent(playlist = playlist)
    }
}

@Composable
private fun ExpandToggleButton(
    isExpanded: Boolean,
    totalCount: Int,
    onToggle: () -> Unit
) {
    TextButton(
        onClick = onToggle,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = if (isExpanded) "Ver menos" else "Ver todo ($totalCount)",
            color = MutedTeal,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}
