package com.essence.essenceapp.feature.playlist.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.essence.essenceapp.feature.playlist.domain.usecase.DeletePlaylistUseCase
import com.essence.essenceapp.feature.playlist.domain.usecase.GetPlaylistsByUserUseCase
import com.essence.essenceapp.shared.ui.components.status.error.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PlaylistListViewModel @Inject constructor(
    private val getPlaylistsUseCase: GetPlaylistsByUserUseCase,
    private val deletePlaylistUseCase: DeletePlaylistUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlaylistListUiState>(PlaylistListUiState.Loading)
    val uiState: StateFlow<PlaylistListUiState> = _uiState.asStateFlow()

    init {
        loadPlaylists()
    }

    fun onAction(action: PlaylistListAction) {
        when (action) {
            is PlaylistListAction.Refresh -> loadPlaylists()
            is PlaylistListAction.DeletePlaylist -> deletePlaylist(action.id)
            is PlaylistListAction.CreatePlaylist -> Unit
            is PlaylistListAction.OpenDetail -> Unit
            PlaylistListAction.OpenHistory -> Unit
        }
    }

    private fun loadPlaylists() {
        viewModelScope.launch {
            _uiState.value = PlaylistListUiState.Loading

            try {
                val result = getPlaylistsUseCase()
                result.onSuccess { playlists ->
                    _uiState.value = PlaylistListUiState.Success(playlist = playlists)
                }
                result.onFailure { error ->
                    _uiState.value = PlaylistListUiState.Error(
                        message = error.toUserMessage()
                    )
                }
            } catch (e: Exception) {
                _uiState.value = PlaylistListUiState.Error(
                    message = e.toUserMessage()
                )
            }
        }
    }

    private fun deletePlaylist(id: Long) {
        viewModelScope.launch {
            try {
                val result = deletePlaylistUseCase(id)
                result.onSuccess {
                    loadPlaylists()
                }
                result.onFailure { error ->
                    _uiState.value = PlaylistListUiState.Error(
                        message = error.toUserMessage()
                    )
                }
            } catch (e: Exception) {
                _uiState.value = PlaylistListUiState.Error(
                    message = e.toUserMessage()
                )
            }
        }
    }
}