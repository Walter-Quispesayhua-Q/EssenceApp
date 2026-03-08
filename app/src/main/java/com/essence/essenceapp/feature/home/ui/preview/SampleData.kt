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
            title = "jbs",
            durationMs = 210000,
            hlsMasterKey = "key-1",
            imageKey = "img-1",
            songType = "single",
            totalPlays = 1500000,
            artistName = "Bad Bunny",
            albumName = "Un Verano Sin Ti",
            releaseDate = LocalDate.of(2025, 5, 6)
        )
    )

    val albums = listOf(
        AlbumSimple(
            id = 1,
            title = "Un Verano Sin Ti",
            imageKey = "album-img-1",
            albumUrl = "https://example.com/album/1",
            artists = listOf("Bad Bunny"),
            release = LocalDate.of(2025, 5, 6)
        )
    )

    val artists = listOf(
        ArtistSimple(
            id = 1,
            nameArtist = "Bad Bunny",
            imageKey = "artist-img-1",
            artistUrl = "https://example.com/artist/1"
        )
    )

    // Al final — ahora sí existen songs, albums, artists
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
