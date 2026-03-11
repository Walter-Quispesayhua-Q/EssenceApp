package com.essence.essenceapp.feature.playlist.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.essence.essenceapp.feature.playlist.domain.usecase.DeletePlaylistUseCase
import com.essence.essenceapp.feature.playlist.domain.usecase.DeleteSongOfPlaylistUseCase
import com.essence.essenceapp.feature.playlist.domain.usecase.GetListSongsUseCase
import com.essence.essenceapp.feature.playlist.domain.usecase.GetPlaylistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    private val getPlaylistUseCase: GetPlaylistUseCase,
    private val getListSongsUseCase: GetListSongsUseCase,
    private val removeSongUseCase: DeleteSongOfPlaylistUseCase,
    private val deletePlaylistUseCase: DeletePlaylistUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlaylistDetailUiState>(PlaylistDetailUiState.Loading)
    val uiState: StateFlow<PlaylistDetailUiState> = _uiState.asStateFlow()

    fun loadPlaylist(id: Long) {
        viewModelScope.launch {
            _uiState.value = PlaylistDetailUiState.Loading

            val result = getPlaylistUseCase(id)
            result.onSuccess { playlist ->
                _uiState.value = PlaylistDetailUiState.Success(playlist = playlist)
            }
            result.onFailure { error ->
                _uiState.value = PlaylistDetailUiState.Error(
                    message = error.message ?: "Error al cargar playlist"
                )
            }
        }
    }

    fun onAction(action: PlaylistDetailAction) {
        when (action) {
            is PlaylistDetailAction.EditPlaylist -> Unit
            is PlaylistDetailAction.DeletePlaylist -> Unit
            is PlaylistDetailAction.RemoveSong -> removeSong(action.songId)
        }
    }

    private fun removeSong(songId: Long) {
        viewModelScope.launch {
            val currentPlaylist = (_uiState.value as? PlaylistDetailUiState.Success)
                ?.playlist ?: return@launch

            val result = removeSongUseCase(currentPlaylist.id, songId)
            result.onSuccess {
                loadPlaylist(currentPlaylist.id)
            }
        }
    }
}
