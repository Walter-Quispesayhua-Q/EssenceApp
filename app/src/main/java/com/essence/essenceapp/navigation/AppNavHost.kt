package com.essence.essenceapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.essence.essenceapp.feature.home.ui.HomeScreen

@Composable
fun AppNavHost(
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navHostController,
        startDestination = AppNavRoutes.HOME,
        modifier = modifier
    ) {
        composable(route = AppNavRoutes.HOME) {
            HomeScreen()
        }
    }
}