package com.essence.essenceapp.feature.search.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.essence.essenceapp.feature.search.ui.SearchScreen

fun NavGraphBuilder.searchGraph() {
    navigation(
        route = SearchGraphRoutes.SEARCH_GRAPH,
        startDestination = SearchRoutes.SEARCH
    ) {
        composable(route = SearchRoutes.SEARCH) {
            SearchScreen()
        }
    }
}