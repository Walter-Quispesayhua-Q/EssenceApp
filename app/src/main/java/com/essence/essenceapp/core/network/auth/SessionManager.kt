package com.essence.essenceapp.core.network.auth

import com.essence.essenceapp.core.storage.TokenManager
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@Singleton
class SessionManager @Inject constructor(
    private val tokenManager: TokenManager
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val isHandlingExpiration = AtomicBoolean(false)

    private val _sessionExpiredEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpiredEvents: SharedFlow<Unit> = _sessionExpiredEvents.asSharedFlow()

    fun onSessionExpired() {
        if (!isHandlingExpiration.compareAndSet(false, true)) return

        scope.launch {
            tokenManager.clear()
            _sessionExpiredEvents.emit(Unit)
            isHandlingExpiration.set(false)
        }
    }
}