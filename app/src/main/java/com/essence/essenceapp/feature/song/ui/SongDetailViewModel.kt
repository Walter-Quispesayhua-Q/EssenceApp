package com.essence.essenceapp.feature.song.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.essence.essenceapp.feature.song.domain.usecase.GetSongUseCase
import com.essence.essenceapp.feature.song.ui.manager.PlaybackManager
import com.essence.essenceapp.feature.song.ui.manager.SongDetailManagerAction
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SongDetailViewModel @Inject constructor(
    private val getSongUseCase: GetSongUseCase,
    private val playbackManager: PlaybackManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<SongDetailUiState>(SongDetailUiState.Loading)
    val uiState: StateFlow<SongDetailUiState> = _uiState.asStateFlow()

    private var currentSongId: Long? = null

    init {
        viewModelScope.launch {
            playbackManager.uiState.collect { playback ->
                val current = _uiState.value
                if (current is SongDetailUiState.Success) {
                    _uiState.value = current.copy(playback = playback)
                }
            }
        }
    }

    fun loadSong(id: Long) {
        currentSongId = id
        viewModelScope.launch {
            _uiState.value = SongDetailUiState.Loading
            val result = getSongUseCase(id)
            result.onSuccess { song ->
                _uiState.value = SongDetailUiState.Success(
                    song = song,
                    playback = playbackManager.uiState.value
                )
            }
            result.onFailure { e ->
                _uiState.value = SongDetailUiState.Error(e.message ?: "Error al cargar canción")
            }
        }
    }

    fun onAction(action: SongDetailAction) {
        when (action) {
            SongDetailAction.Back -> Unit
            SongDetailAction.Refresh -> currentSongId?.let(::loadSong)
            is SongDetailAction.OpenAlbum -> Unit
            is SongDetailAction.OpenArtist -> Unit
        }
    }

    fun onManagerAction(action: SongDetailManagerAction) {
        playbackManager.onAction(action)
    }
}
