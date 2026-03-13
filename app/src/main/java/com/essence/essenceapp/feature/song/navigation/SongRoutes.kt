package com.essence.essenceapp.feature.song.navigation

object SongRoutes {
    const val SONG_ID = "songId"
    private const val SONG_DETAIL_BASE = "song_detail"
    const val SONG_DETAIL = "$SONG_DETAIL_BASE/{$SONG_ID}"
    fun detail(id: Long): String = "$SONG_DETAIL_BASE/$id"
}