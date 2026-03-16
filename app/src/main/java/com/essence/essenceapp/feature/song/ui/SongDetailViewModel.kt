package com.essence.essenceapp.feature.song.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.essence.essenceapp.feature.song.domain.usecase.AddLikeSongUseCase
import com.essence.essenceapp.feature.song.domain.usecase.DeleteLikeSongUseCase
import com.essence.essenceapp.feature.song.domain.usecase.GetSongUseCase
import com.essence.essenceapp.feature.song.ui.manager.PlaybackManager
import com.essence.essenceapp.feature.song.ui.manager.SongDetailManagerAction
import com.essence.essenceapp.shared.ui.components.status.error.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SongDetailViewModel @Inject constructor(
    private val getSongUseCase: GetSongUseCase,
    private val playbackManager: PlaybackManager,
    private val addLikeSongUseCase: AddLikeSongUseCase,
    private val deleteLikeSongUseCase: DeleteLikeSongUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<SongDetailUiState>(SongDetailUiState.Loading)
    val uiState: StateFlow<SongDetailUiState> = _uiState.asStateFlow()

    private var currentSongLookup: String? = null

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

    fun loadSong(lookup: String) {
        currentSongLookup = lookup
        viewModelScope.launch {
            _uiState.value = SongDetailUiState.Loading
            try {
                val result = getSongUseCase(lookup)
                result.onSuccess { song ->
                    _uiState.value = SongDetailUiState.Success(
                        song = song,
                        playback = playbackManager.uiState.value,
                        isLikeSubmitting = false
                    )
                }
                result.onFailure { e ->
                    _uiState.value = SongDetailUiState.Error(e.toUserMessage())
                }
            } catch (e: Exception) {
                _uiState.value = SongDetailUiState.Error(e.toUserMessage())
            }
        }
    }

    fun onAction(action: SongDetailAction) {
        when (action) {
            SongDetailAction.Back -> Unit
            SongDetailAction.Refresh -> currentSongLookup?.let(::loadSong)
            is SongDetailAction.OpenAlbum -> Unit
            is SongDetailAction.OpenArtist -> Unit
            SongDetailAction.ToggleLike -> toggleLike()
        }
    }

    fun onManagerAction(action: SongDetailManagerAction) {
        playbackManager.onAction(action)
    }

    private fun toggleLike() {
        val current = _uiState.value as? SongDetailUiState.Success ?: return
        if (current.isLikeSubmitting) return

        viewModelScope.launch {
            _uiState.value = current.copy(isLikeSubmitting = true)

            val result = try {
                if (current.song.isLiked) {
                    deleteLikeSongUseCase(current.song.id)
                } else {
                    addLikeSongUseCase(current.song.id)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }

            result.onSuccess {
                val latest = _uiState.value as? SongDetailUiState.Success ?: return@onSuccess
                _uiState.value = latest.copy(
                    song = latest.song.copy(isLiked = !current.song.isLiked),
                    isLikeSubmitting = false
                )
            }

            result.onFailure {
                _uiState.value = current.copy(isLikeSubmitting = false)
            }
        }
    }
}