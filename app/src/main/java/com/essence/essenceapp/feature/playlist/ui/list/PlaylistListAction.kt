package com.essence.essenceapp.feature.playlist.ui.list

sealed interface PlaylistListAction {
    data object Refresh : PlaylistListAction
    data object CreatePlaylist : PlaylistListAction
    data object OpenHistory : PlaylistListAction
    data class OpenDetail(val id: Long) : PlaylistListAction
    data class DeletePlaylist(val id: Long) : PlaylistListAction
}
