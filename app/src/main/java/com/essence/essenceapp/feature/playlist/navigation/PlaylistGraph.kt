package com.essence.essenceapp.feature.playlist.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.essence.essenceapp.feature.history.ui.HistoryScreen
import com.essence.essenceapp.feature.playback.domain.PlaybackAction
import com.essence.essenceapp.feature.playback.domain.PlaybackController
import com.essence.essenceapp.feature.playback.domain.PlaybackOpenRequest
import com.essence.essenceapp.feature.playback.domain.currentHlsMasterKey
import com.essence.essenceapp.feature.playlist.ui.addsongs.PlaylistAddSongsScreen
import com.essence.essenceapp.feature.playlist.ui.detail.PlaylistDetailScreen
import com.essence.essenceapp.feature.playlist.ui.form.PlaylistFormSheet
import com.essence.essenceapp.feature.playlist.ui.list.PlaylistListScreen
import com.essence.essenceapp.feature.song.navigation.SongRoutes
import com.essence.essenceapp.ui.shell.components.OffscreenSurface

fun NavGraphBuilder.playlistGraph(
    navController: NavController,
    playbackController: PlaybackController
) {
    navigation(
        route = PlaylistGraphRoutes.PLAYLIST_GRAPH,
        startDestination = PlaylistRoutes.PLAYLIST_LIST
    ) {
        composable(route = PlaylistRoutes.PLAYLIST_LIST) { backStackEntry ->
            OffscreenSurface {
                PlaylistListScreen(
                    navBackStackEntry = backStackEntry,
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
        }

        composable(route = PlaylistRoutes.PLAYLIST_HISTORY) {
            OffscreenSurface {
                HistoryScreen(
                    onBack = { navController.popBackStack() },
                    onOpenSong = { request ->
                        playbackController.dispatch(PlaybackAction.Open(request))
                        request.currentHlsMasterKey()?.let { hlsMasterKey ->
                            navController.navigate(SongRoutes.detail(hlsMasterKey))
                        }
                    }
                )
            }
        }

        composable(
            route = PlaylistRoutes.PLAYLIST_DETAIL,
            arguments = listOf(
                navArgument(PlaylistRoutes.PLAYLIST_ID) { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getLong(PlaylistRoutes.PLAYLIST_ID)
                ?: return@composable

            OffscreenSurface {
                PlaylistDetailScreen(
                    playlistId = playlistId,
                    previousBackStackEntry = navController.previousBackStackEntry,
                    onNavigateToEdit = { id ->
                        navController.navigate(PlaylistRoutes.formEdit(id))
                    },
                    onNavigateToAddSongs = { id ->
                        navController.navigate(PlaylistRoutes.addSongs(id))
                    },
                    onOpenSong = { request ->
                        playbackController.dispatch(PlaybackAction.Open(request))
                        request.currentHlsMasterKey()?.let { hlsMasterKey ->
                            navController.navigate(SongRoutes.detail(hlsMasterKey))
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(
            route = PlaylistRoutes.PLAYLIST_ADD_SONGS,
            arguments = listOf(
                navArgument(PlaylistRoutes.PLAYLIST_ID) { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getLong(PlaylistRoutes.PLAYLIST_ID)
                ?: return@composable

            OffscreenSurface {
                PlaylistAddSongsScreen(
                    playlistId = playlistId,
                    onBack = {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("playlist_updated", true)
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(route = PlaylistRoutes.PLAYLIST_FORM_CREATE) {
            PlaylistFormSheet(
                playlistId = null,
                onDismiss = { navController.popBackStack() },
                onSuccess = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("playlist_updated", true)
                    navController.popBackStack()
                }
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
                onSuccess = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("playlist_updated", true)
                    navController.popBackStack()
                }
            )
        }
    }
}
