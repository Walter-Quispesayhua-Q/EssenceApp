package com.essence.essenceapp.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.essence.essenceapp.feature.home.ui.HomeScreen

fun NavGraphBuilder.homeGraph(
) {
    navigation(
        route = HomeGraphRoutes.HOME_GRAPH,
        startDestination = HomeRoutes.HOME
    ) {
        composable(HomeRoutes.HOME) {
            HomeScreen()
        }
    }
}