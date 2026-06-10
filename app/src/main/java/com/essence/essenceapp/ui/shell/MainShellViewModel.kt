package com.essence.essenceapp.ui.shell

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.essence.essenceapp.core.network.ConnectivityEvent
import com.essence.essenceapp.core.network.ConnectivityNotifier
import com.essence.essenceapp.core.network.auth.SessionManager
import com.essence.essenceapp.core.playback.PlaybackUiEvent
import com.essence.essenceapp.core.playback.PlaybackUiNotifier
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
    private val sessionManager: SessionManager,
    private val connectivityNotifier: ConnectivityNotifier,
    private val playbackUiNotifier: PlaybackUiNotifier
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _sessionExpiredEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpiredEvent = _sessionExpiredEvent.asSharedFlow()

    private val _authRequiredEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val authRequiredEvent = _authRequiredEvent.asSharedFlow()

    private val _guestPlayEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val guestPlayEvent = _guestPlayEvent.asSharedFlow()

    private val _connectivityEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val connectivityEvent = _connectivityEvent.asSharedFlow()

    private val _unavailableSkippingEvent = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val unavailableSkippingEvent = _unavailableSkippingEvent.asSharedFlow()

    private var lastConnectivityNotifyAt: Long = 0L
    private var lastUnavailableSkipNotifyAt: Long = 0L

    init {
        observeAuthState()
        observeSessionExpiration()
        observeAuthRequired()
        observeConnectivity()
        observePlaybackUi()
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

    private fun observeConnectivity() {
        viewModelScope.launch {
            connectivityNotifier.events.collectLatest { event ->
                if (event !is ConnectivityEvent.Unstable) return@collectLatest
                val now = System.currentTimeMillis()
                if (now - lastConnectivityNotifyAt >= CONNECTIVITY_NOTIFY_MIN_INTERVAL_MS) {
                    lastConnectivityNotifyAt = now
                    _connectivityEvent.emit(Unit)
                }
            }
        }
    }

    private fun observePlaybackUi() {
        viewModelScope.launch {
            playbackUiNotifier.events.collectLatest { event ->
                when (event) {
                    is PlaybackUiEvent.UnavailableSkipping -> {
                        val now = System.currentTimeMillis()
                        if (now - lastUnavailableSkipNotifyAt >= UNAVAILABLE_SKIP_MIN_INTERVAL_MS) {
                            lastUnavailableSkipNotifyAt = now
                            _unavailableSkippingEvent.emit(event.songTitle)
                        }
                    }
                }
            }
        }
    }

    private companion object {
        const val CONNECTIVITY_NOTIFY_MIN_INTERVAL_MS = 5_000L
        const val UNAVAILABLE_SKIP_MIN_INTERVAL_MS = 5_000L
    }
}