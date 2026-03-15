package com.essence.essenceapp.feature.playlist.navigation

object PlaylistRoutes {
    const val PLAYLIST_ID = "playlistId"

    private const val BASE = "playlist"

    const val PLAYLIST_LIST = "$BASE/list"
    const val PLAYLIST_DETAIL = "$BASE/detail/{$PLAYLIST_ID}"
    const val PLAYLIST_HISTORY = "$BASE/history"

    const val PLAYLIST_FORM_CREATE = "$BASE/form"
    const val PLAYLIST_FORM_EDIT = "$BASE/form/{$PLAYLIST_ID}"

    fun detail(id: Long): String = "$BASE/detail/$id"
    fun formEdit(id: Long): String = "$BASE/form/$id"
}