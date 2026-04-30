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
import com.essence.essenceapp.feature.song.ui.playback.manager.PlaybackManager
import com.essence.essenceapp.ui.shell.shellEnterTransition
import com.essence.essenceapp.ui.shell.shellExitTransition
import com.essence.essenceapp.ui.shell.shellPopEnterTransition
import com.essence.essenceapp.ui.shell.shellPopExitTransition

@Composable
fun MainTabsNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    playbackManager: PlaybackManager,
    isLoggedIn: Boolean,
    onRequireAuth: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = HomeGraphRoutes.HOME_GRAPH,
        modifier = modifier,
        enterTransition = { shellEnterTransition },
        exitTransition = { shellExitTransition },
        popEnterTransition = { shellPopEnterTransition },
        popExitTransition = { shellPopExitTransition }
    ) {
        homeGraph(
            navController = navController,
            playbackManager = playbackManager,
            isLoggedIn = isLoggedIn,
            onRequireAuth = onRequireAuth
        )

        searchGraph(
            navController = navController,
            playbackManager = playbackManager,
            isLoggedIn = isLoggedIn,
            onRequireAuth = onRequireAuth
        )

        playlistGraph(
            navController = navController,
            playbackManager = playbackManager
        )

        profileGraph()

        songGraph(
            navController = navController,
            isLoggedIn = isLoggedIn,
            onRequireAuth = onRequireAuth
        )

        artistGraph(
            navController = navController,
            playbackManager = playbackManager
        )
        albumGraph(
            navController = navController,
            playbackManager = playbackManager
        )

        historyGraph(
            navController = navController,
            playbackManager = playbackManager,
            isLoggedIn = isLoggedIn,
            onRequireAuth = onRequireAuth
        )
    }
}