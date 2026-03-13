package com.essence.essenceapp.feature.song.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.essence.essenceapp.feature.song.ui.SongDetailScreen

fun NavGraphBuilder.songGraph(
    navController: NavController
) {
    navigation(
        route = SongGraphRoutes.SONG_GRAPH,
        startDestination = SongRoutes.SONG_DETAIL
    ) {
        composable(
            route = SongRoutes.SONG_DETAIL,
            arguments = listOf(
                navArgument(SongRoutes.SONG_ID) { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val args = backStackEntry.arguments
            val songId = args
                ?.takeIf { it.containsKey(SongRoutes.SONG_ID) }
                ?.getLong(SongRoutes.SONG_ID)

            if (songId == null) {
                LaunchedEffect(Unit) { navController.popBackStack() }
                return@composable
            }

            SongDetailScreen(
                songId = songId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}