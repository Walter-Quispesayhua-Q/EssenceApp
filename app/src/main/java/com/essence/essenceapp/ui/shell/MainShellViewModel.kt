package com.essence.essenceapp.ui.shell

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.essence.essenceapp.core.network.auth.SessionManager
import com.essence.essenceapp.core.storage.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class MainShellViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _sessionExpiredEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpiredEvent = _sessionExpiredEvent.asSharedFlow()

    private val _authRequiredEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val authRequiredEvent = _authRequiredEvent.asSharedFlow()

    private val _guestPlayEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val guestPlayEvent = _guestPlayEvent.asSharedFlow()

    init {
        observeAuthState()
        observeSessionExpiration()
        observeAuthRequired()
    }

    fun notifyGuestPlayAttempt() {
        _guestPlayEvent.tryEmit(Unit)
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            tokenManager.token.collectLatest { token ->
                _isLoggedIn.value = !token.isNullOrBlank()
            }
        }
    }

    private fun observeSessionExpiration() {
        viewModelScope.launch {
            sessionManager.sessionExpiredEvents.collectLatest {
                _sessionExpiredEvent.emit(Unit)
            }
        }
    }

    private fun observeAuthRequired() {
        viewModelScope.launch {
            sessionManager.authRequiredEvents.collectLatest {
                _authRequiredEvent.emit(Unit)
            }
        }
    }
}