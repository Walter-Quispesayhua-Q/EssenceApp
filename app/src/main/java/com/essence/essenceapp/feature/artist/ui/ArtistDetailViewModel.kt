package com.essence.essenceapp.feature.artist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.essence.essenceapp.feature.artist.domain.usecase.GetArtistUseCase
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
class ArtistDetailViewModel @Inject constructor(
    private val getArtistUseCase: GetArtistUseCase,
    private val addLikeArtistUseCase: AddLikeSongUseCase,
    private val deleteLikeArtistUseCase: DeleteLikeSongUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ArtistDetailUiState>(ArtistDetailUiState.Loading)
    val uiState: StateFlow<ArtistDetailUiState> = _uiState.asStateFlow()

    private var currentArtistId: Long? = null

    fun loadArtist(id: Long) {
        currentArtistId = id
        viewModelScope.launch {
            _uiState.value = ArtistDetailUiState.Loading
            try {
                val result = getArtistUseCase(id)
                result.onSuccess { artist ->
                    _uiState.value = ArtistDetailUiState.Success(
                        artist = artist,
                        isLikeSubmitting = false
                    )
                }
                result.onFailure { error ->
                    _uiState.value = ArtistDetailUiState.Error(error.toUserMessage())
                }
            } catch (e: Exception) {
                _uiState.value = ArtistDetailUiState.Error(e.toUserMessage())
            }
        }
    }

    fun onAction(action: ArtistDetailAction) {
        when (action) {
            ArtistDetailAction.Refresh -> currentArtistId?.let(::loadArtist)
            ArtistDetailAction.Back -> Unit
            is ArtistDetailAction.OpenSong -> Unit
            is ArtistDetailAction.OpenAlbum -> Unit
            ArtistDetailAction.ToggleLike -> toggleLike()
        }
    }
    private fun toggleLike() {
        val current = _uiState.value as? ArtistDetailUiState.Success ?: return
        if (current.isLikeSubmitting) return

        viewModelScope.launch {
            _uiState.value = current.copy(isLikeSubmitting = true)

            val result = try {
                if (current.artist.isLiked) {
                    deleteLikeArtistUseCase(current.artist.id)
                } else {
                    addLikeArtistUseCase(current.artist.id)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }

            result.onSuccess {
                val latest = _uiState.value as? ArtistDetailUiState.Success ?: return@onSuccess
                _uiState.value = latest.copy(
                    artist = latest.artist.copy(isLiked = !current.artist.isLiked),
                    isLikeSubmitting = false
                )
            }

            result.onFailure {
                _uiState.value = current.copy(isLikeSubmitting = false)
            }
        }
    }
}
