package com.essence.essenceapp.feature.history.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.essence.essenceapp.feature.history.ui.HistoryScreen
import com.essence.essenceapp.feature.song.navigation.SongRoutes

fun NavGraphBuilder.historyGraph(
    navController: NavController,
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

            HistoryScreen(
                onBack = { navController.popBackStack() },
                onOpenSong = { songId ->
                    navController.navigate(SongRoutes.detail(songId))
                }
            )
        }
    }
}