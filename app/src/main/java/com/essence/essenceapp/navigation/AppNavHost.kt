package com.essence.essenceapp.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.essence.essenceapp.feature.auth.navigation.AuthGraphRoutes
import com.essence.essenceapp.feature.auth.navigation.authGraph
import com.essence.essenceapp.feature.song.ui.playback.manager.PlaybackManager
import com.essence.essenceapp.ui.shell.MainShellScreen
import com.essence.essenceapp.ui.splash.SplashScreen

private const val ROOT_ENTER_DURATION_MS = 115
private const val ROOT_EXIT_DURATION_MS = 90

object RootRoutes {
    const val SPLASH = "splash"
    const val MAIN_SHELL = "main_shell"
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    playbackManager: PlaybackManager
) {
    val navController = rememberNavController()

    val openMainShell: () -> Unit = {
        navController.navigate(RootRoutes.MAIN_SHELL) {
            popUpTo(RootRoutes.MAIN_SHELL) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = RootRoutes.SPLASH,
        modifier = modifier,
        enterTransition = {
            fadeIn(
                animationSpec = tween(
                    durationMillis = ROOT_ENTER_DURATION_MS,
                    easing = FastOutSlowInEasing
                )
            )
        },
        exitTransition = {
            fadeOut(
                animationSpec = tween(
                    durationMillis = ROOT_EXIT_DURATION_MS,
                    easing = FastOutSlowInEasing
                )
            )
        },
        popEnterTransition = {
            fadeIn(
                animationSpec = tween(
                    durationMillis = ROOT_ENTER_DURATION_MS,
                    easing = FastOutSlowInEasing
                )
            )
        },
        popExitTransition = {
            fadeOut(
                animationSpec = tween(
                    durationMillis = ROOT_EXIT_DURATION_MS,
                    easing = FastOutSlowInEasing
                )
            )
        }
    ) {
        composable(route = RootRoutes.SPLASH) {
            SplashScreen(
                onFinished = {
                    navController.navigate(RootRoutes.MAIN_SHELL) {
                        popUpTo(RootRoutes.SPLASH) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(route = RootRoutes.MAIN_SHELL) {
            MainShellScreen(
                playbackManager = playbackManager,
                onRequireAuth = {
                    navController.navigate(AuthGraphRoutes.AUTH_GRAPH) {
                        launchSingleTop = true
                    }
                }
            )
        }

        authGraph(
            navController = navController,
            onExitAuth = openMainShell,
            onLoginSuccess = openMainShell
        )
    }
}