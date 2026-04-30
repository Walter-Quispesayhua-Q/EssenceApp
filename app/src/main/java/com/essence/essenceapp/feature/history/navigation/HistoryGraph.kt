package com.essence.essenceapp.feature.history.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.essence.essenceapp.feature.history.ui.HistoryScreen
import com.essence.essenceapp.feature.song.navigation.SongRoutes
import com.essence.essenceapp.feature.song.ui.playback.manager.PlaybackManager
import com.essence.essenceapp.ui.shell.components.OffscreenSurface

fun NavGraphBuilder.historyGraph(
    navController: NavController,
    playbackManager: PlaybackManager,
    isLoggedIn: Boolean,
    onRequireAuth: () -> Unit
) {
    navigation(
        route = HistoryGraphRoutes.HISTORY_GRAPH,
        startDestination = HistoryRoutes.HISTORY_LIST
    ) {
        composable(route = HistoryRoutes.HISTORY_LIST) {
            if (!isLoggedIn) {
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                    onRequireAuth()
                }
                return@composable
            }

            OffscreenSurface {
                HistoryScreen(
                    onBack = { navController.popBackStack() },
                    onOpenSong = { request ->
                        playbackManager.setQueueFromItems(
                            items = request.queue,
                            startIndex = request.startIndex,
                            sourceKey = request.sourceKey
                        )
                        navController.navigate(SongRoutes.detail(request.songLookup)) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}