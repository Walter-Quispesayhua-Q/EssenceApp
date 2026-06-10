package com.essence.essenceapp.core.network

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@Singleton
class ConnectivityNotifier @Inject constructor() {

    private val _events = MutableSharedFlow<ConnectivityEvent>(
        replay = 0,
        extraBufferCapacity = 4,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events: SharedFlow<ConnectivityEvent> = _events.asSharedFlow()

    fun notifyUnstable() {
        _events.tryEmit(ConnectivityEvent.Unstable)
    }
}

sealed interface ConnectivityEvent {
    data object Unstable : ConnectivityEvent
}
