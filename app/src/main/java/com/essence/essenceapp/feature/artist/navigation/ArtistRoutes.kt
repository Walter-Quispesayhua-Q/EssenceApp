package com.essence.essenceapp.feature.artist.navigation

import android.net.Uri

object ArtistRoutes {
    const val ARTIST_LOOKUP = "artistLookup"
    private const val ARTIST_DETAIL_BASE = "artist_detail"

    const val ARTIST_DETAIL = "$ARTIST_DETAIL_BASE?$ARTIST_LOOKUP={$ARTIST_LOOKUP}"

    fun detail(lookup: String): String {
        return "$ARTIST_DETAIL_BASE?$ARTIST_LOOKUP=${Uri.encode(lookup)}"
    }
}