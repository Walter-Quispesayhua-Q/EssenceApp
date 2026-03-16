package com.essence.essenceapp.feature.playlist.ui.list.componets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.QueueMusic
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.playlist.domain.model.PlaylistSimple
import com.essence.essenceapp.feature.playlist.domain.model.PlaylistsSimples
import com.essence.essenceapp.feature.playlist.ui.list.PlaylistListAction
import com.essence.essenceapp.feature.playlist.ui.list.PlaylistListUiState
import com.essence.essenceapp.shared.ui.components.cards.playlist.CompactPlaylistContent
import com.essence.essenceapp.ui.shell.LocalBottomBarClearance
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import com.essence.essenceapp.ui.theme.LuxeGold
import com.essence.essenceapp.ui.theme.MutedTeal
import com.essence.essenceapp.ui.theme.SoftRose

private sealed interface PlaylistSectionState {
    data object Loading : PlaylistSectionState
    data class Error(val message: String) : PlaylistSectionState
    data class Data(val items: List<PlaylistSimple>) : PlaylistSectionState
}

private data class QuickAccessItem(
    val title: String,
    val value: String?,
    val icon: ImageVector,
    val tint: Color,
    val badge: String? = null,
    val action: PlaylistListAction? = null
)

private val quickAccessItems = listOf(
    QuickAccessItem(
        title = "Canciones que te gustan",
        value = "0",
        icon = Icons.Default.Favorite,
        tint = SoftRose
    ),
    QuickAccessItem(
        title = "Escuchado recientemente",
        value = "Hoy",
        icon = Icons.Default.History,
        tint = MutedTeal,
        action = PlaylistListAction.OpenHistory
    ),
    QuickAccessItem(
        title = "Descargas",
        value = null,
        icon = Icons.Default.Download,
        tint = LuxeGold,
        badge = "Próximamente"
    )
)

@Composable
fun PlaylistListContent(
    modifier: Modifier = Modifier,
    state: PlaylistListUiState,
    onAction: (PlaylistListAction) -> Unit
) {
    val (mySection, publicSection) = mapStateToSections(state)
    val bottomClearance = LocalBottomBarClearance.current

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = bottomClearance + 20.dp)
    ) {
        item {
            QuickAccessIsland(
                items = quickAccessItems,
                onAction = onAction,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item { GradientSectionDivider() }

        item {
            SectionHeader(
                title = "Mis Playlists",
                trailing = counterText(mySection),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item {
            PlaylistSectionBody(
                state = mySection,
                emptyTitle = "Aún no tienes playlists",
                emptySubtitle = "Crea tu primera playlist con el botón +",
                isPublicSection = false,
                onAction = onAction,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item { GradientSectionDivider() }

        item {
            SectionHeader(
                title = "Playlists Públicas",
                subtitle = "Curadas por la comunidad",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item {
            PlaylistSectionBody(
                state = publicSection,
                emptyTitle = "No hay playlists públicas",
                emptySubtitle = "Cuando existan, aparecerán aquí.",
                isPublicSection = true,
                onAction = onAction,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun QuickAccessIsland(
    items: List<QuickAccessItem>,
    onAction: (PlaylistListAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            items.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (item.action != null) Modifier.clickable { onAction(item.action) }
                            else Modifier
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = item.tint,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (item.badge != null) {
                        Surface(
                            color = item.tint.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = item.badge,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = item.tint
                            )
                        }
                    } else if (!item.value.isNullOrBlank()) {
                        Text(
                            text = item.value,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                if (index < items.size - 1) {
                    IslandDivider()
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String? = null,
    trailing: String? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = if (subtitle == null) Alignment.CenterVertically else Alignment.Top
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(18.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(MutedTeal, MutedTeal.copy(alpha = 0.3f))
                        ),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                    )
                }
            }
        }

        if (!trailing.isNullOrBlank()) {
            Text(
                text = trailing,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun GradientSectionDivider() {
    Box(
        modifier = Modifier
            .padding(horizontal = 32.dp)
            .fillMaxWidth()
            .height(0.5.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                        Color.Transparent
                    )
                )
            )
    )
}

@Composable
private fun PlaylistSectionBody(
    state: PlaylistSectionState,
    emptyTitle: String,
    emptySubtitle: String,
    isPublicSection: Boolean,
    onAction: (PlaylistListAction) -> Unit,
    modifier: Modifier = Modifier
) {
    when (state) {
        PlaylistSectionState.Loading -> SectionLoadingContent(modifier = modifier)
        is PlaylistSectionState.Error -> SectionErrorContent(
            message = state.message,
            onRetry = { onAction(PlaylistListAction.Refresh) },
            modifier = modifier
        )
        is PlaylistSectionState.Data -> {
            if (state.items.isEmpty()) {
                EmptySectionContent(
                    title = emptyTitle,
                    subtitle = emptySubtitle,
                    modifier = modifier
                )
            } else if (isPublicSection) {
                PublicPlaylistsIsland(
                    playlists = state.items,
                    onOpen = { onAction(PlaylistListAction.OpenDetail(it)) },
                    modifier = modifier
                )
            } else {
                MyPlaylistsIsland(
                    playlists = state.items,
                    onOpen = { onAction(PlaylistListAction.OpenDetail(it)) },
                    onDelete = { onAction(PlaylistListAction.DeletePlaylist(it)) },
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
private fun MyPlaylistsIsland(
    playlists: List<PlaylistSimple>,
    onOpen: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            playlists.forEachIndexed { index, playlist ->
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = { value ->
                        if (value == SwipeToDismissBoxValue.EndToStart) {
                            onDelete(playlist.id)
                            true
                        } else false
                    }
                )

                SwipeToDismissBox(
                    state = dismissState,
                    backgroundContent = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.12f))
                                .padding(end = 72.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.error.copy(alpha = 0.18f)
                            ) {
//                                Icon(
//                                    imageVector = Icons.Default.Delete,
//                                    contentDescription = "Eliminar",
//                                    tint = MaterialTheme.colorScheme.error,
//                                    modifier = Modifier.padding(10.dp)
//                                )
                            }
                        }
                    },
                    enableDismissFromStartToEnd = false,
                    enableDismissFromEndToStart = true
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                            .clickable { onOpen(playlist.id) }
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        CompactPlaylistContent(playlist = playlist)
                    }
                }

                if (index < playlists.size - 1) {
                    IslandDivider()
                }
            }
        }
    }
}

@Composable
private fun PublicPlaylistsIsland(
    playlists: List<PlaylistSimple>,
    onOpen: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val previewCount = 3
    val hasMore = playlists.size > previewCount

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            playlists.take(previewCount).forEachIndexed { index, playlist ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpen(playlist.id) }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    CompactPlaylistContent(playlist = playlist)
                }
                if (index < minOf(previewCount, playlists.size) - 1 || isExpanded) {
                    IslandDivider()
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    playlists.drop(previewCount).forEachIndexed { index, playlist ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpen(playlist.id) }
                                .padding(horizontal = 14.dp, vertical = 12.dp)
                        ) {
                            CompactPlaylistContent(playlist = playlist)
                        }
                        if (index < playlists.size - previewCount - 1) {
                            IslandDivider()
                        }
                    }
                }
            }

            if (hasMore) {
                IslandDivider()
                TextButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isExpanded) "Ver menos"
                        else "Ver todo (${playlists.size})",
                        color = MutedTeal,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionLoadingContent(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = MutedTeal
            )
            Text(
                text = "Cargando playlists...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun SectionErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.ErrorOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            OutlinedButton(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, MutedTeal),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MutedTeal)
            ) {
                Text(
                    text = "Reintentar",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun EmptySectionContent(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.QueueMusic,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun IslandDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 14.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
    )
}

private fun mapStateToSections(
    state: PlaylistListUiState
): Pair<PlaylistSectionState, PlaylistSectionState> =
    when (state) {
        is PlaylistListUiState.Idle,
        is PlaylistListUiState.Loading ->
            PlaylistSectionState.Loading to PlaylistSectionState.Loading

        is PlaylistListUiState.Error ->
            PlaylistSectionState.Error(state.message) to PlaylistSectionState.Error(state.message)

        is PlaylistListUiState.Success ->
            PlaylistSectionState.Data(state.playlist.myPlaylists.orEmpty()) to
                    PlaylistSectionState.Data(state.playlist.playlistsPublic.orEmpty())
    }

private fun counterText(state: PlaylistSectionState): String =
    when (state) {
        PlaylistSectionState.Loading -> "..."
        is PlaylistSectionState.Error -> "--"
        is PlaylistSectionState.Data -> state.items.size.toString()
    }

private val previewMyPlaylists = listOf(
    PlaylistSimple(id = 1L, title = "Lo-Fi Study", isPublic = false, totalLikes = 12),
    PlaylistSimple(id = 2L, title = "Gym Power", isPublic = true, totalLikes = 40),
    PlaylistSimple(id = 3L, title = "Road Trip", isPublic = false, totalLikes = 8)
)

private val previewPublicPlaylists = listOf(
    PlaylistSimple(id = 11L, title = "Chill Nights", isPublic = true, totalLikes = 120),
    PlaylistSimple(id = 12L, title = "Indie Essentials", isPublic = true, totalLikes = 95),
    PlaylistSimple(id = 13L, title = "Focus Mode", isPublic = true, totalLikes = 67),
    PlaylistSimple(id = 14L, title = "Latin Vibes", isPublic = true, totalLikes = 210),
    PlaylistSimple(id = 15L, title = "Electronic Dreams", isPublic = true, totalLikes = 88)
)

@Preview(name = "Playlist List - Success", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PlaylistListSuccessPreview() {
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

@Preview(name = "Playlist List - Loading", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PlaylistListLoadingPreview() {
    EssenceAppTheme {
        PlaylistListContent(state = PlaylistListUiState.Loading, onAction = {})
    }
}

@Preview(name = "Playlist List - Empty", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PlaylistListEmptyPreview() {
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