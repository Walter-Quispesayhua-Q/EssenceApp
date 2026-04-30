package com.essence.essenceapp.feature.playlist.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.essence.essenceapp.feature.playlist.domain.usecase.AddLikePlaylistUseCase
import com.essence.essenceapp.feature.playlist.domain.usecase.DeleteLikePlaylistUseCase
import com.essence.essenceapp.feature.playlist.domain.usecase.DeletePlaylistUseCase
import com.essence.essenceapp.feature.playlist.domain.usecase.DeleteSongOfPlaylistUseCase
import com.essence.essenceapp.feature.playlist.domain.usecase.GetListSongsUseCase
import com.essence.essenceapp.feature.playlist.domain.usecase.GetPlaylistUseCase
import com.essence.essenceapp.shared.ui.components.status.error.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    private val getPlaylistUseCase: GetPlaylistUseCase,
    private val getListSongsUseCase: GetListSongsUseCase,
    private val removeSongUseCase: DeleteSongOfPlaylistUseCase,
    private val deletePlaylistUseCase: DeletePlaylistUseCase,
    private val addLikePlaylistUseCase: AddLikePlaylistUseCase,
    private val deleteLikePlaylistUseCase: DeleteLikePlaylistUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlaylistDetailUiState>(PlaylistDetailUiState.Loading)
    val uiState: StateFlow<PlaylistDetailUiState> = _uiState.asStateFlow()

    private var currentPlaylistId: Long? = null

    fun loadPlaylist(id: Long) {
        currentPlaylistId = id

        viewModelScope.launch {
            val previous = _uiState.value
            val isSamePlaylistRefresh = previous is PlaylistDetailUiState.Success &&
                previous.playlist.id == id

            if (isSamePlaylistRefresh) {
                _uiState.value = (previous as PlaylistDetailUiState.Success).copy(
                    isRefreshing = true
                )
            } else {
                _uiState.value = PlaylistDetailUiState.Loading
            }

            try {
                val playlistDeferred = async { getPlaylistUseCase(id) }
                val songsDeferred = async { getListSongsUseCase(id) }

                val playlistResult = playlistDeferred.await()
                val songsResult = songsDeferred.await()

                playlistResult.onSuccess { playlist ->
                    val songs = songsResult.getOrDefault(emptyList())
                    _uiState.value = PlaylistDetailUiState.Success(
                        playlist = playlist,
                        songs = songs,
                        isSongsLoading = false,
                        isRefreshing = false,
                        isLikeSubmitting = false
                    )
                }

                playlistResult.onFailure { error ->
                    if (isSamePlaylistRefresh) {
                        _uiState.value = (previous as PlaylistDetailUiState.Success).copy(
                            isRefreshing = false
                        )
                    } else {
                        _uiState.value = PlaylistDetailUiState.Error(
                            message = error.toUserMessage()
                        )
                    }
                }
            } catch (e: Exception) {
                if (isSamePlaylistRefresh) {
                    _uiState.value = (previous as PlaylistDetailUiState.Success).copy(
                        isRefreshing = false
                    )
                } else {
                    _uiState.value = PlaylistDetailUiState.Error(
                        message = e.toUserMessage()
                    )
                }
            }
        }
    }

    fun onAction(action: PlaylistDetailAction) {
        when (action) {
            PlaylistDetailAction.EditPlaylist -> Unit
            PlaylistDetailAction.AddSongs -> Unit
            PlaylistDetailAction.DeletePlaylist -> deleteCurrentPlaylist()
            is PlaylistDetailAction.RemoveSong -> removeSong(action.songId)
            is PlaylistDetailAction.OpenSong -> Unit
            PlaylistDetailAction.ToggleLike -> toggleLike()
        }
    }

    private fun removeSong(songId: Long) {
        viewModelScope.launch {
            val current = _uiState.value as? PlaylistDetailUiState.Success ?: return@launch

            try {
                val result = removeSongUseCase(current.playlist.id, songId)
                result.onSuccess {
                    loadPlaylist(current.playlist.id)
                }
                result.onFailure { error ->
                    _uiState.value = PlaylistDetailUiState.Error(
                        message = error.toUserMessage()
                    )
                }
            } catch (e: Exception) {
                _uiState.value = PlaylistDetailUiState.Error(
                    message = e.toUserMessage()
                )
            }
        }
    }

    private fun deleteCurrentPlaylist() {
        val current = _uiState.value as? PlaylistDetailUiState.Success ?: return
        if (current.isDeleting) return

        viewModelScope.launch {
            val id = currentPlaylistId ?: return@launch

            _uiState.value = current.copy(isDeleting = true, deleteError = null)

            val result = try {
                deletePlaylistUseCase(id)
            } catch (e: Exception) {
                Result.failure(e)
            }

            result.onSuccess {
                _uiState.value = PlaylistDetailUiState.Deleted
            }

            result.onFailure { error ->
                val latest = _uiState.value as? PlaylistDetailUiState.Success ?: return@onFailure
                _uiState.value = latest.copy(
                    isDeleting = false,
                    deleteError = error.toUserMessage()
                )
            }
        }
    }

    fun clearDeleteError() {
        val current = _uiState.value as? PlaylistDetailUiState.Success ?: return
        if (current.deleteError == null) return
        _uiState.value = current.copy(deleteError = null)
    }

    private fun toggleLike() {
        val current = _uiState.value as? PlaylistDetailUiState.Success ?: return
        if (!current.playlist.isPublic || current.isLikeSubmitting) return

        viewModelScope.launch {
            _uiState.value = current.copy(isLikeSubmitting = true)

            val result = try {
                if (current.playlist.isLiked) {
                    deleteLikePlaylistUseCase(current.playlist.id)
                } else {
                    addLikePlaylistUseCase(current.playlist.id)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }

            result.onSuccess {
                val latest = _uiState.value as? PlaylistDetailUiState.Success ?: return@onSuccess
                val newLiked = !current.playlist.isLiked
                val currentLikes = latest.playlist.totalLikes ?: 0L

                _uiState.value = latest.copy(
                    playlist = latest.playlist.copy(
                        isLiked = newLiked,
                        totalLikes = if (newLiked) currentLikes + 1 else (currentLikes - 1).coerceAtLeast(0L)
                    ),
                    isLikeSubmitting = false
                )
            }

            result.onFailure {
                _uiState.value = current.copy(isLikeSubmitting = false)
            }
        }
    }
}