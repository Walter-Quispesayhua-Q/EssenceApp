package com.essence.essenceapp.feature.playlist.ui.list.componets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.essence.essenceapp.feature.playlist.domain.model.PlaylistSimple
import com.essence.essenceapp.feature.playlist.domain.model.PlaylistsSimples
import com.essence.essenceapp.feature.playlist.ui.list.PlaylistListAction
import com.essence.essenceapp.feature.playlist.ui.list.PlaylistListUiState
import com.essence.essenceapp.ui.shell.LocalBottomBarClearance
import com.essence.essenceapp.ui.theme.EssenceAppTheme

@Composable
fun PlaylistListContent(
    modifier: Modifier = Modifier,
    state: PlaylistListUiState,
    onAction: (PlaylistListAction) -> Unit
) {
    val (mySection, publicSection) = state.toSections()
    val bottomClearance = LocalBottomBarClearance.current

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = bottomClearance + 20.dp)
    ) {
        item {
            QuickAccessIsland(
                items = defaultQuickAccessItems,
                onAction = onAction
            )
        }

        item { GradientSectionDivider() }

        item {
            SectionHeader(
                title = "Mis Playlists",
                trailing = mySection.counterText(),
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
        is PlaylistSectionState.Data -> when {
            state.items.isEmpty() -> EmptySectionContent(
                title = emptyTitle,
                subtitle = emptySubtitle,
                modifier = modifier
            )
            isPublicSection -> PublicPlaylistsIsland(
                playlists = state.items,
                onOpen = { onAction(PlaylistListAction.OpenDetail(it)) },
                modifier = modifier
            )
            else -> MyPlaylistsIsland(
                playlists = state.items,
                onOpen = { onAction(PlaylistListAction.OpenDetail(it)) },
                modifier = modifier
            )
        }
    }
}

private val previewMyPlaylists = listOf(
    PlaylistSimple(id = 0L, title = "Liked Songs", isPublic = false, type = "LIKED"),
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
