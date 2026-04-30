package com.essence.essenceapp.ui.resilience

sealed interface CrashRecoveryState {
    data object Idle : CrashRecoveryState

    data class Showing(
        val title: String,
        val message: String,
        val canRetry: Boolean,
        val retryCount: Int
    ) : CrashRecoveryState
}
