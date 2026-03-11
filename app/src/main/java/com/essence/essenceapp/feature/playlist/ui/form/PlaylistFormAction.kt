package com.essence.essenceapp.feature.playlist.ui.form

sealed interface PlaylistFormAction {
    data class TitleChanged(val value: String) : PlaylistFormAction
    data class DescriptionChanged(val value: String) : PlaylistFormAction
    data class IsPublicChanged(val value: Boolean) : PlaylistFormAction
    data object Submit : PlaylistFormAction
    data object ClearError : PlaylistFormAction
}
