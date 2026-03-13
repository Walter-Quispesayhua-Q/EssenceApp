package com.essence.essenceapp.ui.shell.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.essence.essenceapp.feature.home.navigation.HomeGraphRoutes
import com.essence.essenceapp.feature.home.navigation.homeGraph
import com.essence.essenceapp.feature.playlist.navigation.playlistGraph
import com.essence.essenceapp.feature.search.navigation.searchGraph

@Composable
fun MainTabsNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeGraphRoutes.HOME_GRAPH,
        modifier = modifier
    ) {
        homeGraph()
        searchGraph()
        playlistGraph(navController)
//        profileGraph(navController) // cuando lo tengas
    }
}
