package com.essence.essenceapp.ui.resilience

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val MAX_RETRIES_BEFORE_FORCED_HOME = 2

@HiltViewModel
class CrashRecoveryViewModel @Inject constructor(
    private val crashReporter: CrashReporter
) : ViewModel() {

    private val _state = MutableStateFlow<CrashRecoveryState>(CrashRecoveryState.Idle)
    val state: StateFlow<CrashRecoveryState> = _state.asStateFlow()

    private val _navigateHomeEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateHomeEvent: SharedFlow<Unit> = _navigateHomeEvent.asSharedFlow()

    private val _retrySignal = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val retrySignal: SharedFlow<Unit> = _retrySignal.asSharedFlow()

    private var retryCount = 0

    init {
        viewModelScope.launch {
            crashReporter.events.collectLatest { event ->
                showRecovery(event)
            }
        }
    }

    private fun showRecovery(event: CrashEvent) {
        val canRetry = retryCount < MAX_RETRIES_BEFORE_FORCED_HOME && !event.isFatal
        _state.value = CrashRecoveryState.Showing(
            title = if (canRetry) "Algo no salió como esperábamos" else "Volvamos al inicio",
            message = if (canRetry) {
                "No te preocupes, esto pasa a veces. Intenta de nuevo o vuelve al inicio."
            } else {
                "Detectamos un problema persistente. Te llevamos al inicio para continuar."
            },
            canRetry = canRetry,
            retryCount = retryCount
        )
    }

    fun onRetry() {
        retryCount++
        crashReporter.clear()
        _state.value = CrashRecoveryState.Idle
        viewModelScope.launch { _retrySignal.emit(Unit) }
    }

    fun onGoHome() {
        retryCount = 0
        crashReporter.clear()
        _state.value = CrashRecoveryState.Idle
        viewModelScope.launch { _navigateHomeEvent.emit(Unit) }
    }

    fun onRecovered() {
        retryCount = 0
    }
}
