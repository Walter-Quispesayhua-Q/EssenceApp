package com.essence.essenceapp.feature.auth.register.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.essence.essenceapp.feature.auth.register.ui.components.RegisterContent

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    onRegisterSuccess: () -> Unit = {}

) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state) {
        if (state is RegisterUiState.Success) onRegisterSuccess()
    }
    Scaffold { innerPadding ->
        RegisterContent(
            modifier = Modifier.padding(innerPadding),
            state = state,
            onAction = viewModel::onAction
        )
    }
}