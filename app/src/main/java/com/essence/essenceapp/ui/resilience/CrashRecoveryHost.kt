package com.essence.essenceapp.ui.resilience

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CrashRecoveryHost(
    modifier: Modifier = Modifier,
    onNavigateHome: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val viewModel: CrashRecoveryViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    var contentKey by remember { mutableIntStateOf(0) }

    LaunchedEffect(viewModel) {
        viewModel.retrySignal.collectLatest {
            contentKey++
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.navigateHomeEvent.collectLatest {
            contentKey++
            onNavigateHome()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        key(contentKey) {
            content()
        }

        when (val current = state) {
            is CrashRecoveryState.Showing -> {
                CrashRecoveryDialog(
                    title = current.title,
                    message = current.message,
                    canRetry = current.canRetry,
                    onRetry = viewModel::onRetry,
                    onGoHome = viewModel::onGoHome
                )
            }
            CrashRecoveryState.Idle -> Unit
        }
    }
}
