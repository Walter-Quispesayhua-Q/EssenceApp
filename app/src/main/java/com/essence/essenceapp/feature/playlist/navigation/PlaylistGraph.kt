package com.essence.essenceapp.feature.playlist.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.essence.essenceapp.feature.history.ui.HistoryScreen
import com.essence.essenceapp.feature.playlist.ui.detail.PlaylistDetailScreen
import com.essence.essenceapp.feature.playlist.ui.form.PlaylistFormSheet
import com.essence.essenceapp.feature.playlist.ui.list.PlaylistListScreen
import com.essence.essenceapp.feature.song.navigation.SongRoutes

fun NavGraphBuilder.playlistGraph(
    navController: NavController
) {
    navigation(
        route = PlaylistGraphRoutes.PLAYLIST_GRAPH,
        startDestination = PlaylistRoutes.PLAYLIST_LIST
    ) {
        composable(route = PlaylistRoutes.PLAYLIST_LIST) {
            PlaylistListScreen(
                onNavigateToDetail = { id ->
                    navController.navigate(PlaylistRoutes.detail(id))
                },
                onNavigateToCreate = {
                    navController.navigate(PlaylistRoutes.PLAYLIST_FORM_CREATE)
                },
                onNavigateToHistory = {
                    navController.navigate(PlaylistRoutes.PLAYLIST_HISTORY)
                }
            )
        }

        composable(route = PlaylistRoutes.PLAYLIST_HISTORY) {
            HistoryScreen(
                onBack = { navController.popBackStack() },
                onOpenSong = { songId ->
                    navController.navigate(SongRoutes.detail(songId))
                }
            )
        }

        composable(
            route = PlaylistRoutes.PLAYLIST_DETAIL,
            arguments = listOf(
                navArgument(PlaylistRoutes.PLAYLIST_ID) { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getLong(PlaylistRoutes.PLAYLIST_ID)
                ?: return@composable

            PlaylistDetailScreen(
                playlistId = playlistId,
                onNavigateToEdit = { id ->
                    navController.navigate(PlaylistRoutes.formEdit(id))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(route = PlaylistRoutes.PLAYLIST_FORM_CREATE) {
            PlaylistFormSheet(
                playlistId = null,
                onDismiss = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }

        composable(
            route = PlaylistRoutes.PLAYLIST_FORM_EDIT,
            arguments = listOf(
                navArgument(PlaylistRoutes.PLAYLIST_ID) { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getLong(PlaylistRoutes.PLAYLIST_ID)
                ?: return@composable

            PlaylistFormSheet(
                playlistId = playlistId,
                onDismiss = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }
    }
}