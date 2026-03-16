package com.essence.essenceapp.feature.playlist.ui.form

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.essence.essenceapp.feature.playlist.domain.model.PlaylistRequest
import com.essence.essenceapp.feature.playlist.domain.usecase.CreatePlaylistUseCase
import com.essence.essenceapp.feature.playlist.domain.usecase.GetForUpdateUseCase
import com.essence.essenceapp.feature.playlist.domain.usecase.UpdatePlaylistUseCase
import com.essence.essenceapp.shared.ui.components.status.error.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PlaylistFormViewModel @Inject constructor(
    private val createPlaylistUseCase: CreatePlaylistUseCase,
    private val updatePlaylistUseCase: UpdatePlaylistUseCase,
    private val getForUpdateUseCase: GetForUpdateUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlaylistFormUiState>(
        PlaylistFormUiState.Editing()
    )
    val uiState: StateFlow<PlaylistFormUiState> = _uiState.asStateFlow()

    private var editingId: Long? = null

    fun loadForEdit(id: Long) {
        editingId = id

        viewModelScope.launch {
            _uiState.value = PlaylistFormUiState.Editing(isSubmitting = true)

            try {
                Log.e("PLAYLIST_EDIT_DEBUG", "loadForEdit id=$id")

                val result = getForUpdateUseCase(id)

                result.onSuccess { playlist ->
                    Log.e("PLAYLIST_EDIT_DEBUG", "loadForEdit success playlistId=${playlist.id}")

                    _uiState.value = PlaylistFormUiState.Editing(
                        form = PlaylistFormState(
                            title = playlist.title,
                            description = playlist.description ?: "",
                            isPublic = playlist.isPublic
                        )
                    )
                }

                result.onFailure { error ->
                    Log.e("PLAYLIST_EDIT_DEBUG", "loadForEdit failure=${error.message}", error)

                    _uiState.value = PlaylistFormUiState.Editing(
                        errorMessage = error.toUserMessage()
                    )
                }
            } catch (e: Exception) {
                Log.e("PLAYLIST_EDIT_DEBUG", "loadForEdit exception=${e.message}", e)

                _uiState.value = PlaylistFormUiState.Editing(
                    errorMessage = e.toUserMessage()
                )
            }
        }
    }

    fun onAction(action: PlaylistFormAction) {
        when (action) {
            is PlaylistFormAction.TitleChanged -> updateEditing {
                it.copy(form = it.form.copy(title = action.value))
            }
            is PlaylistFormAction.DescriptionChanged -> updateEditing {
                it.copy(form = it.form.copy(description = action.value))
            }
            is PlaylistFormAction.IsPublicChanged -> updateEditing {
                it.copy(form = it.form.copy(isPublic = action.value))
            }
            PlaylistFormAction.Submit -> submit()
            PlaylistFormAction.ClearError -> updateEditing {
                it.copy(errorMessage = null)
            }
        }
    }

    private fun submit() {
        val current = _uiState.value
        if (current !is PlaylistFormUiState.Editing) return

        viewModelScope.launch {
            updateEditing { it.copy(isSubmitting = true) }

            val request = PlaylistRequest(
                title = current.form.title,
                description = current.form.description,
                isPublic = current.form.isPublic
            )

            try {
                Log.e("PLAYLIST_EDIT_DEBUG", "submit editingId=$editingId title=${request.title} isPublic=${request.isPublic}")

                val result = if (editingId != null) {
                    updatePlaylistUseCase(editingId!!, request)
                } else {
                    createPlaylistUseCase(request)
                }

                result.onSuccess {
                    Log.e("PLAYLIST_EDIT_DEBUG", "submit success")
                    _uiState.value = PlaylistFormUiState.Success
                }

                result.onFailure { error ->
                    Log.e("PLAYLIST_EDIT_DEBUG", "submit failure=${error.message}", error)

                    updateEditing {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = error.message
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("PLAYLIST_EDIT_DEBUG", "submit exception=${e.message}", e)

                updateEditing {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = e.toUserMessage()
                    )
                }
            }
        }
    }

    private fun updateEditing(
        transform: (PlaylistFormUiState.Editing) -> PlaylistFormUiState.Editing
    ) {
        val current = _uiState.value
        if (current is PlaylistFormUiState.Editing) {
            _uiState.value = transform(current)
        }
    }

    fun initialize(playlistId: Long?) {
        if (playlistId == null) {
            editingId = null
            _uiState.value = PlaylistFormUiState.Editing()
        } else {
            loadForEdit(playlistId)
        }
    }
}