package com.essence.essenceapp.ui.shell.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.essence.essenceapp.feature.home.navigation.HomeGraphRoutes
import com.essence.essenceapp.feature.playlist.navigation.PlaylistGraphRoutes
import com.essence.essenceapp.feature.profile.navigation.ProfileGraphRoutes
import com.essence.essenceapp.feature.search.navigation.SearchGraphRoutes

data class TopLevelDestination(
    val graphRoute: String,
    val label: String,
    val icon: ImageVector,
    val requiresAuth: Boolean = false
)

object TopLevelDestinations {
    private val publicItems: List<TopLevelDestination> = listOf(
        TopLevelDestination(
            graphRoute = HomeGraphRoutes.HOME_GRAPH,
            label = "Home",
            icon = Icons.Default.Home
        ),
        TopLevelDestination(
            graphRoute = SearchGraphRoutes.SEARCH_GRAPH,
            label = "Search",
            icon = Icons.Default.Search
        )
    )

    private val privateItems: List<TopLevelDestination> = listOf(
        TopLevelDestination(
            graphRoute = PlaylistGraphRoutes.PLAYLIST_GRAPH,
            label = "Biblioteca",
            icon = Icons.Default.LibraryMusic,
            requiresAuth = true
        ),
        TopLevelDestination(
            graphRoute = ProfileGraphRoutes.PROFILE_GRAPH,
            label = "Perfil",
            icon = Icons.Default.Person,
            requiresAuth = true
        )
    )

    val items: List<TopLevelDestination> = publicItems + privateItems

    fun itemsFor(isLoggedIn: Boolean): List<TopLevelDestination> {
        return if (isLoggedIn) items else publicItems
    }
}