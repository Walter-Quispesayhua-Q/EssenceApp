package com.essence.essenceapp.feature.song.navigation

import android.net.Uri

object SongRoutes {
    const val HLS_MASTER_KEY = "hlsMasterKey"
    private const val SONG_DETAIL_BASE = "song_detail"

    const val SONG_DETAIL = "$SONG_DETAIL_BASE?$HLS_MASTER_KEY={$HLS_MASTER_KEY}"

    fun detail(hlsMasterKey: String): String {
        return "$SONG_DETAIL_BASE?$HLS_MASTER_KEY=${Uri.encode(hlsMasterKey)}"
    }
}