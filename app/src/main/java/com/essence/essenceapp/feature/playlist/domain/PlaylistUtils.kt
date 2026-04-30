package com.essence.essenceapp.feature.playlist.domain

object PlaylistUtils {

    const val TYPE_NORMAL = "NORMAL"
    const val TYPE_LIKED = "LIKED"

    fun isSystemPlaylist(type: String): Boolean {
        return type == TYPE_LIKED
    }

    fun getDisplayTitle(type: String, originalTitle: String): String {
        return when (type) {
            TYPE_LIKED -> "Canciones que te gustan"
            else -> originalTitle
        }
    }
}
