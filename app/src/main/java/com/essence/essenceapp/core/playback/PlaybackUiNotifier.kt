package com.essence.essenceapp.core.playback

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@Singleton
class PlaybackUiNotifier @Inject constructor() {

    private val _events = MutableSharedFlow<PlaybackUiEvent>(
        replay = 0,
        extraBufferCapacity = 4,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events: SharedFlow<PlaybackUiEvent> = _events.asSharedFlow()

    fun notifyUnavailableSkipping(songTitle: String) {
        _events.tryEmit(PlaybackUiEvent.UnavailableSkipping(songTitle))
    }
}

sealed interface PlaybackUiEvent {
    data class UnavailableSkipping(val songTitle: String) : PlaybackUiEvent
}
