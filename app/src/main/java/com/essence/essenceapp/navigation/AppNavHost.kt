package com.essence.essenceapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.essence.essenceapp.feature.auth.navigation.AuthGraphRoutes
import com.essence.essenceapp.feature.auth.navigation.authGraph
import com.essence.essenceapp.feature.song.ui.manager.PlaybackManager
import com.essence.essenceapp.ui.shell.MainShellScreen

object RootRoutes {
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
        startDestination = RootRoutes.MAIN_SHELL,
        modifier = modifier
    ) {
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