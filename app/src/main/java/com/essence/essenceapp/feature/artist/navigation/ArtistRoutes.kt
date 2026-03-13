package com.essence.essenceapp.feature.artist.navigation

object ArtistRoutes {
    const val ARTIST_ID = "artistId"
    private const val ARTIST_DETAIL_BASE = "artist_detail"
    const val ARTIST_DETAIL = "$ARTIST_DETAIL_BASE/{$ARTIST_ID}"
    fun detail(id: Long): String = "$ARTIST_DETAIL_BASE/$id"
}
