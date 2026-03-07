package com.essence.essenceapp.feature.home.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.essence.essenceapp.feature.home.ui.components.HomeContent
import com.essence.essenceapp.feature.home.ui.components.HomeTopBar


@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { HomeTopBar(title = "Mi App") },
    ) {
        innerPadding ->
        HomeContent(
            modifier = Modifier.padding(innerPadding),
            state = state,
            onRefresh = {viewModel.onRefresh()}
        )
    }
}