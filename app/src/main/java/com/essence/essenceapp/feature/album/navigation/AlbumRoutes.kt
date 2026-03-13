package com.essence.essenceapp.feature.album.navigation

object AlbumRoutes {
    const val ALBUM_ID = "albumId"
    private const val ALBUM_DETAIL_BASE = "album_detail"
    const val ALBUM_DETAIL = "$ALBUM_DETAIL_BASE/{$ALBUM_ID}"
    fun detail(id: Long): String = "$ALBUM_DETAIL_BASE/$id"
}