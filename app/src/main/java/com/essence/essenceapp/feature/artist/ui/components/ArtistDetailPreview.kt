package com.essence.essenceapp.feature.artist.ui.components

import com.essence.essenceapp.feature.album.domain.model.AlbumSimple
import com.essence.essenceapp.feature.artist.domain.model.Artist
import com.essence.essenceapp.feature.song.domain.model.SongSimple
import java.time.LocalDate

internal val previewArtistSongsSample: List<SongSimple> = listOf(
    SongSimple(1L, "Titi Me Pregunto", 210_000, "songs/titi/master.m3u8", null, "single", 1_200_000_000L, "Bad Bunny", "Un Verano Sin Ti", LocalDate.of(2022, 5, 6)),
    SongSimple(2L, "Moscow Mule", 245_000, "songs/moscow/master.m3u8", null, "album", 980_000_000L, "Bad Bunny", "Un Verano Sin Ti", LocalDate.of(2022, 5, 6)),
    SongSimple(3L, "Ojitos Lindos", 224_000, "songs/ojitos/master.m3u8", null, "album", 870_000_000L, "Bad Bunny", "Un Verano Sin Ti", LocalDate.of(2022, 5, 6)),
    SongSimple(4L, "Me Porto Bonito", 178_000, "songs/bonito/master.m3u8", null, "album", 2_100_000_000L, "Bad Bunny", "Un Verano Sin Ti", LocalDate.of(2022, 5, 6)),
    SongSimple(5L, "Despues de la Playa", 273_000, "songs/playa/master.m3u8", null, "album", 760_000_000L, "Bad Bunny", "Un Verano Sin Ti", LocalDate.of(2022, 5, 6))
)

internal val previewArtistAlbumsSample: List<AlbumSimple> = listOf(
    AlbumSimple(10L, "Un Verano Sin Ti", null, "albums/uvst", listOf("Bad Bunny"), LocalDate.of(2022, 5, 6)),
    AlbumSimple(11L, "Nadie Sabe Lo Que Va a Pasar Manana", null, "albums/nslqvp", listOf("Bad Bunny"), LocalDate.of(2023, 10, 13)),
    AlbumSimple(12L, "YHLQMDLG", null, "albums/yhlqmdlg", listOf("Bad Bunny"), LocalDate.of(2020, 2, 29)),
    AlbumSimple(13L, "X 100PRE", null, "albums/x100pre", listOf("Bad Bunny"), LocalDate.of(2018, 12, 23))
)

internal val previewArtistSample: Artist = Artist(
    id = 100L,
    nameArtist = "Bad Bunny",
    description = "Uno de los artistas mas influyentes de la musica latina actual, conocido por mezclar reggaeton, trap y generos urbanos con su estilo unico.",
    imageKey = null,
    artistUrl = "artists/bad-bunny",
    country = "Puerto Rico",
    songs = previewArtistSongsSample,
    albums = previewArtistAlbumsSample,
    isLiked = true
)
