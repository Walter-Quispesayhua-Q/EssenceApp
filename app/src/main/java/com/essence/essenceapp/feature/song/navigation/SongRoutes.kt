package com.essence.essenceapp.feature.song.navigation

import android.net.Uri

object SongRoutes {
    const val SONG_LOOKUP = "songLookup"
    private const val SONG_DETAIL_BASE = "song_detail"

    const val SONG_DETAIL = "$SONG_DETAIL_BASE?$SONG_LOOKUP={$SONG_LOOKUP}"

    fun detail(lookup: String): String {
        return "$SONG_DETAIL_BASE?$SONG_LOOKUP=${Uri.encode(lookup)}"
    }
}