package com.essence.essenceapp.ui.shell.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.essence.essenceapp.feature.home.navigation.HomeGraphRoutes
import com.essence.essenceapp.feature.playlist.navigation.PlaylistGraphRoutes
import com.essence.essenceapp.feature.search.navigation.SearchGraphRoutes

data class TopLevelDestination(
    val graphRoute: String,
    val label: String,
    val icon: ImageVector
)

object TopLevelDestinations {
    private const val PROFILE_GRAPH_ROUTE = "profile_graph"

    val items: List<TopLevelDestination> = listOf(
        TopLevelDestination(
            graphRoute = HomeGraphRoutes.HOME_GRAPH,
            label = "Home",
            icon = Icons.Default.Home
        ),
        TopLevelDestination(
            graphRoute = SearchGraphRoutes.SEARCH_GRAPH,
            label = "Search",
            icon = Icons.Default.Search
        ),
        TopLevelDestination(
            graphRoute = PlaylistGraphRoutes.PLAYLIST_GRAPH,
            label = "Biblioteca",
            icon = Icons.Default.LibraryMusic
        ),
        TopLevelDestination(
            graphRoute = PROFILE_GRAPH_ROUTE,
            label = "Perfil",
            icon = Icons.Default.Person
        )
    )
}
