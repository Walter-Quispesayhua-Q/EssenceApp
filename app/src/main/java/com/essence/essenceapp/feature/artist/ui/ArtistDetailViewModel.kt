package com.essence.essenceapp.feature.artist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.essence.essenceapp.feature.artist.domain.usecase.GetArtistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    private val getArtistUseCase: GetArtistUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ArtistDetailUiState>(ArtistDetailUiState.Loading)
    val uiState: StateFlow<ArtistDetailUiState> = _uiState.asStateFlow()

    private var currentArtistId: Long? = null

    fun loadArtist(id: Long) {
        currentArtistId = id
        viewModelScope.launch {
            _uiState.value = ArtistDetailUiState.Loading
            val result = getArtistUseCase(id)
            result.onSuccess { artist ->
                _uiState.value = ArtistDetailUiState.Success(artist)
            }
            result.onFailure { error ->
                _uiState.value = ArtistDetailUiState.Error(
                    message = error.message ?: "Error al cargar artista"
                )
            }
        }
    }

    fun onAction(action: ArtistDetailAction) {
        when (action) {
            ArtistDetailAction.Refresh -> currentArtistId?.let(::loadArtist)
            ArtistDetailAction.Back -> Unit
            is ArtistDetailAction.OpenSong -> Unit
            is ArtistDetailAction.OpenAlbum -> Unit
        }
    }
}
