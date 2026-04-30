package com.essence.essenceapp.feature.album.ui.components

import com.essence.essenceapp.feature.album.domain.model.Album
import com.essence.essenceapp.feature.song.domain.model.SongSimple
import java.time.LocalDate

internal val previewSongsSample: List<SongSimple> = listOf(
    SongSimple(1L, "Titi Me Pregunto", 210_000, "songs/titi/master.m3u8", null, "single", 1_200_000L, "Bad Bunny", "Un Verano Sin Ti", LocalDate.of(2022, 5, 6)),
    SongSimple(2L, "Moscow Mule", 245_000, "songs/moscow/master.m3u8", null, "album", 980_000L, "Bad Bunny", "Un Verano Sin Ti", LocalDate.of(2022, 5, 6)),
    SongSimple(3L, "Ojitos Lindos", 224_000, "songs/ojitos/master.m3u8", null, "album", 870_000L, "Bad Bunny", "Un Verano Sin Ti", LocalDate.of(2022, 5, 6)),
    SongSimple(4L, "Despues de la Playa", 273_000, "songs/playa/master.m3u8", null, "album", 760_000L, "Bad Bunny", "Un Verano Sin Ti", LocalDate.of(2022, 5, 6)),
    SongSimple(5L, "Me Porto Bonito", 178_000, "songs/bonito/master.m3u8", null, "album", 2_100_000L, "Bad Bunny", "Un Verano Sin Ti", LocalDate.of(2022, 5, 6))
)

internal val previewAlbumSample: Album = Album(
    id = 10L,
    title = "Un Verano Sin Ti",
    description = "Album veraniego con una mezcla unica de ritmos latinos y melodias atrapantes.",
    imageKey = null,
    releaseDate = LocalDate.of(2022, 5, 6),
    artists = emptyList(),
    songs = previewSongsSample,
    isLiked = true
)
