package com.essence.essenceapp.feature.profile.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.essence.essenceapp.feature.profile.ui.components.ProfileContent

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onBack: (() -> Unit)? = null
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleResumeEffect(Unit) {
        viewModel.silentRefresh()
        onPauseOrDispose {}
    }

    ProfileContent(
        modifier = Modifier.fillMaxSize(),
        state = state,
        onRetry = viewModel::onRefresh,
        onBack = onBack
    )
}