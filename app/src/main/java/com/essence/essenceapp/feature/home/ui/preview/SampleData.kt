package com.essence.essenceapp.feature.home.ui.preview

import com.essence.essenceapp.feature.album.domain.model.AlbumSimple
import com.essence.essenceapp.feature.artist.domain.model.ArtistSimple
import com.essence.essenceapp.feature.home.domain.model.Home
import com.essence.essenceapp.feature.home.domain.model.HomeStatus
import com.essence.essenceapp.feature.song.domain.model.SongSimple
import java.time.LocalDate

object SampleData {

    val songs = listOf(
        SongSimple(
            id = 1,
            title = "Tití Me Preguntó",
            durationMs = 243000,
            hlsMasterKey = "songs/key-1",
            imageKey = "songs/img-1",
            songType = "single",
            totalPlays = 1_500_000,
            artistName = "Bad Bunny",
            albumName = "Un Verano Sin Ti",
            releaseDate = LocalDate.of(2022, 5, 6)
        ),
        SongSimple(
            id = 2,
            title = "Me Porto Bonito",
            durationMs = 178000,
            hlsMasterKey = "songs/key-2",
            imageKey = "songs/img-2",
            songType = "album",
            totalPlays = 2_350_000,
            artistName = "Bad Bunny",
            albumName = "Un Verano Sin Ti",
            releaseDate = LocalDate.of(2022, 5, 6)
        ),
        SongSimple(
            id = 3,
            title = "Lo Que Pasó a Hawaii",
            durationMs = 205000,
            hlsMasterKey = "songs/key-3",
            imageKey = null,
            songType = "single",
            totalPlays = 860_000,
            artistName = "Bad Bunny",
            albumName = null,
            releaseDate = LocalDate.of(2024, 9, 20)
        ),
        SongSimple(
            id = 4,
            title = "Una Canción Con Un Título Bastante Largo Para Probar Ellipsis",
            durationMs = 198000,
            hlsMasterKey = "songs/key-4",
            imageKey = "songs/img-4",
            songType = "album",
            totalPlays = null,
            artistName = "Feid",
            albumName = "FERXXOCALIPSIS",
            releaseDate = LocalDate.of(2023, 12, 1)
        ),
        SongSimple(
            id = 5,
            title = "LUNA",
            durationMs = 196000,
            hlsMasterKey = "songs/key-5",
            imageKey = "songs/img-5",
            songType = "album",
            totalPlays = 940_000,
            artistName = "Feid",
            albumName = "FERXXOCALIPSIS",
            releaseDate = LocalDate.of(2023, 12, 1)
        ),
        SongSimple(
            id = 6,
            title = "Monaco",
            durationMs = 267000,
            hlsMasterKey = "songs/key-6",
            imageKey = "songs/img-6",
            songType = "album",
            totalPlays = 3_400_000,
            artistName = "Bad Bunny",
            albumName = "Nadie Sabe Lo Que Va a Pasar Mañana",
            releaseDate = LocalDate.of(2023, 10, 13)
        )
    )

    val albums = listOf(
        AlbumSimple(
            id = 1,
            title = "Un Verano Sin Ti",
            imageKey = "albums/img-1",
            albumUrl = "https://example.com/albums/1",
            artists = listOf("Bad Bunny"),
            release = LocalDate.of(2022, 5, 6)
        ),
        AlbumSimple(
            id = 2,
            title = "Nadie Sabe Lo Que Va a Pasar Mañana",
            imageKey = "albums/img-2",
            albumUrl = "https://example.com/albums/2",
            artists = listOf("Bad Bunny"),
            release = LocalDate.of(2023, 10, 13)
        ),
        AlbumSimple(
            id = 3,
            title = "FERXXOCALIPSIS",
            imageKey = "albums/img-3",
            albumUrl = "https://example.com/albums/3",
            artists = listOf("Feid"),
            release = LocalDate.of(2023, 12, 1)
        ),
        AlbumSimple(
            id = 4,
            title = "Colores",
            imageKey = null,
            albumUrl = "https://example.com/albums/4",
            artists = listOf("J Balvin"),
            release = LocalDate.of(2020, 3, 19)
        ),
        AlbumSimple(
            id = 5,
            title = "Álbum De Prueba Con Nombre Largo Para Ver Cómo Responde El Grid",
            imageKey = "albums/img-5",
            albumUrl = "https://example.com/albums/5",
            artists = listOf("Artista 1", "Artista 2"),
            release = null
        )
    )

    val artists = listOf(
        ArtistSimple(
            id = 1,
            nameArtist = "Bad Bunny",
            imageKey = "artists/img-1",
            artistUrl = "https://example.com/artists/1"
        ),
        ArtistSimple(
            id = 2,
            nameArtist = "Feid",
            imageKey = "artists/img-2",
            artistUrl = "https://example.com/artists/2"
        ),
        ArtistSimple(
            id = 3,
            nameArtist = "J Balvin",
            imageKey = null,
            artistUrl = "https://example.com/artists/3"
        ),
        ArtistSimple(
            id = 4,
            nameArtist = "Artista Con Nombre Muy Largo Para Probar Ellipsis",
            imageKey = "artists/img-4",
            artistUrl = "https://example.com/artists/4"
        )
    )

    val home = Home(
        songs = songs,
        albums = albums,
        artists = artists,
        status = HomeStatus(
            songsLoaded = true,
            albumsLoaded = true,
            artistsLoaded = true,
            error = null
        )
    )
}