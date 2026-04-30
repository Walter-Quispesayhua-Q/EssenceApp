package com.essence.essenceapp.feature.search.domain

object SearchType {
    const val SONG = "song"
    const val ALBUM = "album"
    const val ARTIST = "artist"
}

fun normalizeSearchType(type: String): String = when (type.trim().lowercase()) {
    "songs" -> SearchType.SONG
    "albums" -> SearchType.ALBUM
    "artists" -> SearchType.ARTIST
    else -> type.trim().lowercase()
}

fun showSongs(type: String): Boolean =
    type.isBlank() || normalizeSearchType(type) == SearchType.SONG

fun showAlbums(type: String): Boolean =
    type.isBlank() || normalizeSearchType(type) == SearchType.ALBUM

fun showArtists(type: String): Boolean =
    type.isBlank() || normalizeSearchType(type) == SearchType.ARTIST
