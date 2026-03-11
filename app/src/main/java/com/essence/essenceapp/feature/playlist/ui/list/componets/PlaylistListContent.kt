package com.essence.essenceapp.feature.playlist.ui.list.componets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.essence.essenceapp.feature.playlist.domain.model.PlaylistSimple
import com.essence.essenceapp.feature.playlist.domain.model.PlaylistsSimples
import com.essence.essenceapp.feature.playlist.ui.list.PlaylistListAction
import com.essence.essenceapp.feature.playlist.ui.list.PlaylistListUiState
import com.essence.essenceapp.shared.ui.components.cards.BaseCard
import com.essence.essenceapp.shared.ui.components.cards.playlist.CompactPlaylistContent
import com.essence.essenceapp.shared.ui.components.cards.playlist.GridPlaylistContent
import com.essence.essenceapp.ui.theme.EssenceAppTheme

private val AccentColor = Color(0xFF00CED1)
private val DividerColor = Color.White.copy(alpha = 0.06f)

private sealed interface PlaylistSectionState {
    data object Loading : PlaylistSectionState
    data class Error(val message: String) : PlaylistSectionState
    data class Data(val items: List<PlaylistSimple>) : PlaylistSectionState
}

private data class QuickAccessItem(
    val title: String,
    val value: String?,
    val icon: ImageVector
)

private val quickAccessItems = listOf(
    QuickAccessItem(
        title = "Canciones que te gustan",
        value = "0",
        icon = Icons.Default.Favorite
    ),
    QuickAccessItem(
        title = "Escuchado recientemente",
        value = "Hoy",
        icon = Icons.Default.History
    ),
    QuickAccessItem(
        title = "Descargas",
        value = null,
        icon = Icons.Default.Download
    )
)

@Composable
fun PlaylistListContent(
    modifier: Modifier = Modifier,
    state: PlaylistListUiState,
    onAction: (PlaylistListAction) -> Unit
) {
    val (mySection, publicSection) = mapStateToSections(state)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        item { QuickAccessSection(items = quickAccessItems) }

        item { SectionDivider() }

        item {
            SectionContainer(
                title = "Mis Playlists",
                subtitle = null,
                trailing = {
                    Text(
                        text = counterText(mySection),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.82f)
                    )
                }
            ) {
                PlaylistSectionBody(
                    state = mySection,
                    emptyTitle = "Aún no tienes playlists",
                    emptySubtitle = "Crea tu primera playlist con el botón +",
                    isPublicSection = false,
                    onAction = onAction
                )
            }
        }

        item { SectionDivider() }

        item {
            SectionContainer(
                title = "Playlists Públicas",
                subtitle = "Curadas por la comunidad",
                trailing = {
                    TextButton(onClick = {}) {
                        Text(text = "Ver todo", color = AccentColor)
                    }
                }
            ) {
                PlaylistSectionBody(
                    state = publicSection,
                    emptyTitle = "No hay playlists públicas",
                    emptySubtitle = "Cuando existan, aparecerán aquí.",
                    isPublicSection = true,
                    onAction = onAction
                )
            }
        }
    }
}

private fun mapStateToSections(
    state: PlaylistListUiState
): Pair<PlaylistSectionState, PlaylistSectionState> =
    when (state) {
        is PlaylistListUiState.Idle,
        is PlaylistListUiState.Loading -> PlaylistSectionState.Loading to PlaylistSectionState.Loading

        is PlaylistListUiState.Error ->
            PlaylistSectionState.Error(state.message) to PlaylistSectionState.Error(state.message)

        is PlaylistListUiState.Success ->
            PlaylistSectionState.Data(state.playlist.myPlaylists.orEmpty()) to
                    PlaylistSectionState.Data(state.playlist.playlistsPublic.orEmpty())
    }

@Composable
private fun QuickAccessSection(items: List<QuickAccessItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.forEach { item ->
            BaseCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = AccentColor
                        )
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (!item.value.isNullOrBlank()) {
                        Text(
                            text = item.value,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionContainer(
    title: String,
    subtitle: String?,
    trailing: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = if (subtitle == null) Alignment.CenterVertically else Alignment.Top
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
                    )
                }
            }

            trailing()
        }

        content()
    }
}

@Composable
private fun PlaylistSectionBody(
    state: PlaylistSectionState,
    emptyTitle: String,
    emptySubtitle: String,
    isPublicSection: Boolean,
    onAction: (PlaylistListAction) -> Unit
) {
    when (state) {
        PlaylistSectionState.Loading -> SectionLoadingCard()

        is PlaylistSectionState.Error -> SectionErrorCard(
            message = state.message,
            onRetry = { onAction(PlaylistListAction.Refresh) }
        )

        is PlaylistSectionState.Data -> {
            if (state.items.isEmpty()) {
                EmptySectionCard(
                    title = emptyTitle,
                    subtitle = emptySubtitle
                )
            } else if (isPublicSection) {
                PublicPlaylistsRow(
                    playlists = state.items,
                    onOpen = { onAction(PlaylistListAction.OpenDetail(it)) }
                )
            } else {
                MyPlaylistsColumn(
                    playlists = state.items,
                    onOpen = { onAction(PlaylistListAction.OpenDetail(it)) },
                    onDelete = { onAction(PlaylistListAction.DeletePlaylist(it)) }
                )
            }
        }
    }
}

@Composable
private fun MyPlaylistsColumn(
    playlists: List<PlaylistSimple>,
    onOpen: (Long) -> Unit,
    onDelete: (Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        playlists.forEach { playlist ->
            BaseCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpen(playlist.id) },
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CompactPlaylistContent(
                        playlist = playlist,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { onDelete(playlist.id) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar playlist",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PublicPlaylistsRow(
    playlists: List<PlaylistSimple>,
    onOpen: (Long) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(playlists, key = { it.id }) { playlist ->
            BaseCard(
                modifier = Modifier.clickable { onOpen(playlist.id) },
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
            ) {
                GridPlaylistContent(playlist = playlist)
            }
        }
    }
}

@Composable
private fun SectionLoadingCard() {
    BaseCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp
            )
            Text(
                text = "Cargando playlists...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f)
            )
        }
    }
}

@Composable
private fun SectionErrorCard(
    message: String,
    onRetry: () -> Unit
) {
    BaseCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
            Button(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Reintentar")
            }
        }
    }
}

@Composable
private fun EmptySectionCard(
    title: String,
    subtitle: String
) {
    BaseCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
            )
        }
    }
}

@Composable
private fun SectionDivider() {
    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(1.dp)
            .background(DividerColor)
    )
}

private fun counterText(state: PlaylistSectionState): String =
    when (state) {
        PlaylistSectionState.Loading -> "..."
        is PlaylistSectionState.Error -> "--"
        is PlaylistSectionState.Data -> state.items.size.toString()
    }

private val previewMyPlaylists = listOf(
    PlaylistSimple(id = 1L, title = "Lo-Fi Study", isPublic = false, totalLikes = 12),
    PlaylistSimple(id = 2L, title = "Gym Power", isPublic = true, totalLikes = 40)
)

private val previewPublicPlaylists = listOf(
    PlaylistSimple(id = 11L, title = "Chill Nights", isPublic = true, totalLikes = 120),
    PlaylistSimple(id = 12L, title = "Indie Essentials", isPublic = true, totalLikes = 95),
    PlaylistSimple(id = 13L, title = "Focus Mode", isPublic = true, totalLikes = 67)
)

@Preview(name = "Playlist List - Idle", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PlaylistListContentIdlePreview() {
    EssenceAppTheme {
        PlaylistListContent(
            state = PlaylistListUiState.Idle,
            onAction = {}
        )
    }
}

@Preview(name = "Playlist List - Loading", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PlaylistListContentLoadingPreview() {
    EssenceAppTheme {
        PlaylistListContent(
            state = PlaylistListUiState.Loading,
            onAction = {}
        )
    }
}

@Preview(name = "Playlist List - Error", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PlaylistListContentErrorPreview() {
    EssenceAppTheme {
        PlaylistListContent(
            state = PlaylistListUiState.Error(message = "No se pudo cargar las playlists."),
            onAction = {}
        )
    }
}

@Preview(name = "Playlist List - Success (Empty)", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PlaylistListContentSuccessEmptyPreview() {
    EssenceAppTheme {
        PlaylistListContent(
            state = PlaylistListUiState.Success(
                playlist = PlaylistsSimples(
                    myPlaylists = emptyList(),
                    playlistsPublic = emptyList()
                )
            ),
            onAction = {}
        )
    }
}

@Preview(name = "Playlist List - Success (Data)", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PlaylistListContentSuccessDataPreview() {
    EssenceAppTheme {
        PlaylistListContent(
            state = PlaylistListUiState.Success(
                playlist = PlaylistsSimples(
                    myPlaylists = previewMyPlaylists,
                    playlistsPublic = previewPublicPlaylists
                )
            ),
            onAction = {}
        )
    }
}
