package com.essence.essenceapp.ui.shell.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.essence.essenceapp.feature.album.navigation.albumGraph
import com.essence.essenceapp.feature.artist.navigation.artistGraph
import com.essence.essenceapp.feature.history.navigation.historyGraph
import com.essence.essenceapp.feature.home.navigation.HomeGraphRoutes
import com.essence.essenceapp.feature.home.navigation.homeGraph
import com.essence.essenceapp.feature.playlist.navigation.playlistGraph
import com.essence.essenceapp.feature.profile.navigation.profileGraph
import com.essence.essenceapp.feature.search.navigation.searchGraph
import com.essence.essenceapp.feature.song.navigation.songGraph

@Composable
fun MainTabsNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    isLoggedIn: Boolean,
    onRequireAuth: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = HomeGraphRoutes.HOME_GRAPH,
        modifier = modifier
    ) {
        homeGraph(
            navController = navController,
            isLoggedIn = isLoggedIn,
            onRequireAuth = onRequireAuth
        )

        searchGraph(
            navController = navController,
            isLoggedIn = isLoggedIn,
            onRequireAuth = onRequireAuth
        )

        playlistGraph(navController)

        profileGraph()

        songGraph(
            navController = navController,
            isLoggedIn = isLoggedIn,
            onRequireAuth = onRequireAuth
        )

        artistGraph(navController)
        albumGraph(navController)

        historyGraph(
            navController = navController,
            isLoggedIn = isLoggedIn,
            onRequireAuth = onRequireAuth
        )
    }
}