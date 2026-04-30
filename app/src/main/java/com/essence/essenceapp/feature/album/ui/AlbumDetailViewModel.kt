package com.essence.essenceapp.feature.album.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.essence.essenceapp.feature.album.domain.usecase.AddLikeAlbumUseCase
import com.essence.essenceapp.feature.album.domain.usecase.DeleteLikeAlbumUseCase
import com.essence.essenceapp.feature.album.domain.usecase.GetAlbumUseCase
import com.essence.essenceapp.shared.cache.AlbumDetailCache
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
    private val addLikeAlbumUseCase: AddLikeAlbumUseCase,
    private val deleteLikeAlbumUseCase: DeleteLikeAlbumUseCase,
    private val albumDetailCache: AlbumDetailCache
) : ViewModel() {

    private val _uiState = MutableStateFlow<AlbumDetailUiState>(AlbumDetailUiState.Loading)
    val uiState: StateFlow<AlbumDetailUiState> = _uiState.asStateFlow()

    private var currentAlbumLookup: String? = null

    fun loadAlbum(lookup: String) {
        currentAlbumLookup = lookup

        val cached = albumDetailCache.get(lookup)
        if (cached != null) {
            _uiState.value = AlbumDetailUiState.Success(
                album = cached,
                isLikeSubmitting = false
            )
            return
        }

        fetchAlbum(lookup)
    }

    private fun fetchAlbum(lookup: String) {
        viewModelScope.launch {
            _uiState.value = AlbumDetailUiState.Loading
            try {
                val result = getAlbumUseCase(lookup)
                if (lookup != currentAlbumLookup) return@launch
                result.onSuccess { album ->
                    albumDetailCache.put(lookup, album)
                    _uiState.value = AlbumDetailUiState.Success(
                        album = album,
                        isLikeSubmitting = false
                    )
                }
                result.onFailure { error ->
                    _uiState.value = AlbumDetailUiState.Error(error.toUserMessage())
                }
            } catch (e: Exception) {
                if (lookup == currentAlbumLookup) {
                    _uiState.value = AlbumDetailUiState.Error(e.toUserMessage())
                }
            }
        }
    }

    fun onAction(action: AlbumDetailAction) {
        when (action) {
            AlbumDetailAction.Refresh -> currentAlbumLookup?.let { lookup ->
                albumDetailCache.invalidate(lookup)
                fetchAlbum(lookup)
            }
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
                val updatedAlbum = latest.album.copy(isLiked = !current.album.isLiked)
                currentAlbumLookup?.let { albumDetailCache.put(it, updatedAlbum) }
                _uiState.value = latest.copy(
                    album = updatedAlbum,
                    isLikeSubmitting = false
                )
            }

            result.onFailure {
                _uiState.value = current.copy(isLikeSubmitting = false)
            }
        }
    }
}