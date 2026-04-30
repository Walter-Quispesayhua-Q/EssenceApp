package com.essence.essenceapp.feature.playlist.ui.list.componets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.playlist.domain.PlaylistUtils
import com.essence.essenceapp.feature.playlist.domain.model.PlaylistSimple
import com.essence.essenceapp.shared.ui.components.cards.playlist.CompactPlaylistContent

@Composable
internal fun MyPlaylistsIsland(
    playlists: List<PlaylistSimple>,
    onOpen: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val likedPlaylists = playlists.filter { PlaylistUtils.isSystemPlaylist(it.type) }
    val normalPlaylists = playlists.filter { !PlaylistUtils.isSystemPlaylist(it.type) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        likedPlaylists.forEach { liked ->
            LikedPlaylistCard(
                playlist = liked,
                onClick = { onOpen(liked.id) }
            )
        }

        if (normalPlaylists.isNotEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    normalPlaylists.forEachIndexed { index, playlist ->
                        PlaylistRow(
                            playlist = playlist,
                            onOpen = { onOpen(playlist.id) }
                        )
                        if (index < normalPlaylists.size - 1) {
                            IslandDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaylistRow(
    playlist: PlaylistSimple,
    onOpen: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            .clickable(onClick = onOpen)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        CompactPlaylistContent(playlist = playlist)
    }
}
