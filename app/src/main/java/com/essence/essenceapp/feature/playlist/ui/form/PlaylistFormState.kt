package com.essence.essenceapp.feature.playlist.ui.form

data class PlaylistFormState(
    val title: String = "",
    val description: String = "",
    val isPublic: Boolean = false
) {
    val isValid: Boolean
        get() = title.isNotBlank()
}