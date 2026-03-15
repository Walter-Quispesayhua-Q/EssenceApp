package com.essence.essenceapp.feature.album.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.essence.essenceapp.feature.album.domain.usecase.GetAlbumUseCase
import com.essence.essenceapp.feature.song.domain.usecase.AddLikeSongUseCase
import com.essence.essenceapp.feature.song.domain.usecase.DeleteLikeSongUseCase
import com.essence.essenceapp.shared.ui.components.status.error.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    private val getAlbumUseCase: GetAlbumUseCase,
    private val addLikeAlbumUseCase: AddLikeSongUseCase,
    private val deleteLikeAlbumUseCase: DeleteLikeSongUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AlbumDetailUiState>(AlbumDetailUiState.Loading)
    val uiState: StateFlow<AlbumDetailUiState> = _uiState.asStateFlow()

    private var currentAlbumId: Long? = null

    fun loadAlbum(id: Long) {
        currentAlbumId = id
        viewModelScope.launch {
            _uiState.value = AlbumDetailUiState.Loading
            try {
                val result = getAlbumUseCase(id)
                result.onSuccess { album ->
                    _uiState.value = AlbumDetailUiState.Success(
                        album = album,
                        isLikeSubmitting = false
                    )
                }
                result.onFailure { error ->
                    _uiState.value = AlbumDetailUiState.Error(error.toUserMessage())
                }
            } catch (e: Exception) {
                _uiState.value = AlbumDetailUiState.Error(e.toUserMessage())
            }
        }
    }

    fun onAction(action: AlbumDetailAction) {
        when (action) {
            AlbumDetailAction.Refresh -> currentAlbumId?.let(::loadAlbum)
            AlbumDetailAction.Back -> Unit
            is AlbumDetailAction.OpenSong -> Unit
            AlbumDetailAction.ToggleLike -> toggleLike()
        }
    }
    private fun toggleLike() {
        val current = _uiState.value as? AlbumDetailUiState.Success ?: return
        if (current.isLikeSubmitting) return

        viewModelScope.launch {
            _uiState.value = current.copy(isLikeSubmitting = true)

            val result = try {
                if (current.album.isLiked) {
                    deleteLikeAlbumUseCase(current.album.id)
                } else {
                    addLikeAlbumUseCase(current.album.id)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }

            result.onSuccess {
                val latest = _uiState.value as? AlbumDetailUiState.Success ?: return@onSuccess
                _uiState.value = latest.copy(
                    album = latest.album.copy(isLiked = !current.album.isLiked),
                    isLikeSubmitting = false
                )
            }

            result.onFailure {
                _uiState.value = current.copy(isLikeSubmitting = false)
            }
        }
    }
}
