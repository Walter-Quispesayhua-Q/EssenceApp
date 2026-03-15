package com.essence.essenceapp.feature.profile.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.essence.essenceapp.feature.profile.ui.components.ProfileContent
import com.essence.essenceapp.feature.profile.ui.components.ProfileTopBar

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val title = when (val current = state) {
        is ProfileUiState.Success -> current.profile.username
        is ProfileUiState.Error -> "Perfil"
        ProfileUiState.Loading -> "Perfil"
    }

    Scaffold(
        topBar = {
            ProfileTopBar(title = title)
        }
    ) { innerPadding ->
        ProfileContent(
            modifier = Modifier.padding(innerPadding),
            state = state,
            onRetry = viewModel::onRefresh
        )
    }
}