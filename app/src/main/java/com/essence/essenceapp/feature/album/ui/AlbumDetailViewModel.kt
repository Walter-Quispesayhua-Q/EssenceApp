package com.essence.essenceapp.feature.album.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.essence.essenceapp.feature.album.domain.usecase.GetAlbumUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    private val getAlbumUseCase: GetAlbumUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AlbumDetailUiState>(AlbumDetailUiState.Loading)
    val uiState: StateFlow<AlbumDetailUiState> = _uiState.asStateFlow()

    private var currentAlbumId: Long? = null

    fun loadAlbum(id: Long) {
        currentAlbumId = id
        viewModelScope.launch {
            _uiState.value = AlbumDetailUiState.Loading
            val result = getAlbumUseCase(id)
            result.onSuccess { album ->
                _uiState.value = AlbumDetailUiState.Success(album)
            }
            result.onFailure { error ->
                _uiState.value = AlbumDetailUiState.Error(
                    message = error.message ?: "Error al cargar álbum"
                )
            }
        }
    }

    fun onAction(action: AlbumDetailAction) {
        when (action) {
            AlbumDetailAction.Refresh -> currentAlbumId?.let(::loadAlbum)
            AlbumDetailAction.Back -> Unit
            is AlbumDetailAction.OpenSong -> Unit
        }
    }
}
