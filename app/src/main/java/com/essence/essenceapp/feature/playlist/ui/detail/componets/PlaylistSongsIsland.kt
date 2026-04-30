package com.essence.essenceapp.feature.playlist.ui.detail.componets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.song.domain.model.SongSimple
import com.essence.essenceapp.feature.song.ui.components.GlassIsland
import com.essence.essenceapp.shared.ui.components.cards.song.CompactSongContent
import com.essence.essenceapp.ui.theme.GraphiteSurface
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.PureWhite
import com.essence.essenceapp.ui.theme.SoftRose
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
internal fun PlaylistSongsIsland(
    songs: List<SongSimple>,
    isPublic: Boolean,
    isSystem: Boolean,
    isOwner: Boolean,
    onRemove: (Long) -> Unit,
    onSongClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val deletedSongIds = remember { mutableStateListOf<Long>() }
    val canRemove = !isSystem && (isOwner || !isPublic)

    GlassIsland(
        modifier = modifier.animateContentSize(),
        accent = if (isSystem) SoftRose else MutedTeal,
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            songs.forEachIndexed { index, song ->
                val isDeleted = deletedSongIds.contains(song.id)
                val songId = song.id
                val isItemRemovable = canRemove && songId != null

                AnimatedVisibility(
                    visible = !isDeleted,
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column {
                        SongRow(
                            index = index + 1,
                            song = song,
                            isRemovable = isItemRemovable,
                            onRemove = if (isItemRemovable) {
                                {
                                    deletedSongIds.add(songId)
                                    scope.launch {
                                        delay(350)
                                        onRemove(songId)
                                        deletedSongIds.remove(songId)
                                    }
                                }
                            } else null,
                            onClick = { onSongClick(song.detailLookup) }
                        )

                        if (index < songs.lastIndex) {
                            SongRowDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SongRow(
    index: Int,
    song: SongSimple,
    isRemovable: Boolean,
    onRemove: (() -> Unit)?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(GraphiteSurface.copy(alpha = 0.55f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TrackIndex(index = index)

        CompactSongContent(
            modifier = Modifier.weight(1f),
            song = song,
            durationText = formatDuration(song.durationMs.toLong()),
            showAddToPlaylistAction = false,
            trailingContent = if (isRemovable && onRemove != null) {
                {
                    RemoveSongButton(onClick = onRemove)
                }
            } else null
        )
    }
}

@Composable
private fun TrackIndex(index: Int) {
    Text(
        modifier = Modifier
            .width(26.dp)
            .padding(end = 4.dp),
        text = "$index",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Medium,
        color = PureWhite.copy(alpha = 0.38f),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun RemoveSongButton(onClick: () -> Unit) {
    Surface(
        modifier = Modifier.size(30.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.error.copy(alpha = 0.06f),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.32f))
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Quitar canción",
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.72f),
                modifier = Modifier.size(15.dp)
            )
        }
    }
}

@Composable
private fun SongRowDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 14.dp),
        thickness = 0.5.dp,
        color = PureWhite.copy(alpha = 0.05f)
    )
}

private fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
