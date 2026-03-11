package com.essence.essenceapp.feature.playlist.ui.form

sealed interface PlaylistFormUiState {
    data class Editing(
        val form: PlaylistFormState = PlaylistFormState(),
        val isSubmitting: Boolean = false,
        val errorMessage: String? = null
        ): PlaylistFormUiState {
        val canSubmit: Boolean
            get() = form.isValid && !isSubmitting
    }
    data object Success : PlaylistFormUiState
}