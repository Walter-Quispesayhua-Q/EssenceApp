package com.essence.essenceapp.ui.resilience

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@Singleton
class CrashReporter @Inject constructor() {

    private val _events = MutableSharedFlow<CrashEvent>(
        replay = 1,
        extraBufferCapacity = 4
    )
    val events: SharedFlow<CrashEvent> = _events.asSharedFlow()

    fun report(throwable: Throwable, isFatal: Boolean = false) {
        if (throwable is CancellationException) return
        _events.tryEmit(CrashEvent(throwable, isFatal))
    }

    fun clear() {
        _events.resetReplayCache()
    }
}
