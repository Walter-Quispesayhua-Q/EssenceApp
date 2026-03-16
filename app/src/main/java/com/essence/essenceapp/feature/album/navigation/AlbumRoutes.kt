package com.essence.essenceapp.feature.album.navigation

import android.net.Uri

object AlbumRoutes {
    const val ALBUM_LOOKUP = "albumLookup"
    private const val ALBUM_DETAIL_BASE = "album_detail"

    const val ALBUM_DETAIL = "$ALBUM_DETAIL_BASE?$ALBUM_LOOKUP={$ALBUM_LOOKUP}"

    fun detail(lookup: String): String {
        return "$ALBUM_DETAIL_BASE?$ALBUM_LOOKUP=${Uri.encode(lookup)}"
    }
}