package com.essence.essenceapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.essence.essenceapp.feature.auth.navigation.authGraph
import com.essence.essenceapp.feature.home.navigation.HomeGraphRoutes
import com.essence.essenceapp.ui.shell.MainShellScreen

object RootRoutes {
    const val MAIN_SHELL = "main_shell"
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = RootRoutes.MAIN_SHELL,
        modifier = modifier
    ) {
        composable(route = RootRoutes.MAIN_SHELL) {
            MainShellScreen()
        }
        authGraph()
    }
}