package com.essence.essenceapp.feature.home.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.essence.essenceapp.feature.album.navigation.AlbumRoutes
import com.essence.essenceapp.feature.artist.navigation.ArtistRoutes
import com.essence.essenceapp.feature.home.ui.HomeScreen
import com.essence.essenceapp.feature.playback.domain.PlaybackAction
import com.essence.essenceapp.feature.playback.domain.PlaybackController
import com.essence.essenceapp.feature.playback.domain.PlaybackOpenRequest
import com.essence.essenceapp.feature.playback.domain.currentHlsMasterKey
import com.essence.essenceapp.feature.song.navigation.SongRoutes
import com.essence.essenceapp.ui.shell.ShellEmphasizedDecelerate

private const val HOME_FAST_ENTER_MS = 10

fun NavGraphBuilder.homeGraph(
    navController: NavController,
    playbackController: PlaybackController,
    isLoggedIn: Boolean,
    onRequireAuth: () -> Unit,
    onGuestPlayAttempt: () -> Unit
) {
    navigation(
        route = HomeGraphRoutes.HOME_GRAPH,
        startDestination = HomeRoutes.HOME
    ) {
        composable(
            route = HomeRoutes.HOME,
            enterTransition = {
                fadeIn(
                    animationSpec = tween(
                        durationMillis = HOME_FAST_ENTER_MS,
                        easing = ShellEmphasizedDecelerate
                    )
                )
            },
            popEnterTransition = {
                fadeIn(
                    animationSpec = tween(
                        durationMillis = HOME_FAST_ENTER_MS,
                        easing = ShellEmphasizedDecelerate
                    )
                )
            }
        ) {
            HomeScreen(
                isLoggedIn = isLoggedIn,
                onLoginClick = onRequireAuth,
                onOpenSong = { request ->
                    if (!isLoggedIn) {
                        onGuestPlayAttempt()
                        return@HomeScreen
                    }

                    playbackController.dispatch(PlaybackAction.Open(request))
                    request.currentHlsMasterKey()?.let { hlsMasterKey ->
                        navController.navigate(SongRoutes.detail(hlsMasterKey))
                    }
                },
                onOpenAlbum = { albumLookup ->
                    navController.navigate(AlbumRoutes.detail(albumLookup))
                },
                onOpenArtist = { artistLookup ->
                    navController.navigate(ArtistRoutes.detail(artistLookup))
                }
            )
        }
    }
}