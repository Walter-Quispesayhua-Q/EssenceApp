package com.essence.essenceapp.feature.home.data.api

import com.essence.essenceapp.feature.album.data.dto.AlbumResponseSimpleApiDTO
import com.essence.essenceapp.feature.artist.data.dto.ArtistResponseSimpleApiDTO
import com.essence.essenceapp.feature.home.data.dto.HomeResponseApiDTO
import com.essence.essenceapp.feature.home.data.dto.HomeStatusApiDTO
import com.essence.essenceapp.feature.song.data.dto.SongResponseSimpleApiDTO
import java.time.LocalDate

class FakeHomeApiService : HomeApiService {
    override suspend fun getHome(): HomeResponseApiDTO? {
        return HomeResponseApiDTO(
            songs = listOf(
                SongResponseSimpleApiDTO(
                    id = 1,
                    durationMs = 354000,
                    hlsMasterKey = "fake-key-001",
                    imageKey = "fake-image-001",
                    songType = "single",
                    totalPlays = 5000,
                    artistName = "Queen",
                    albumName = "Greatest Hits",
                    releaseDate = LocalDate.of(1975, 10, 31)
                ),
                SongResponseSimpleApiDTO(
                    id = 2,
                    durationMs = 390000,
                    hlsMasterKey = "fake-key-002",
                    imageKey = "fake-image-002",
                    songType = "single",
                    totalPlays = 3000,
                    artistName = "Eagles",
                    albumName = "Hotel California",
                    releaseDate = LocalDate.of(1977, 2, 22)
                )
            ),
            albums = listOf(
                AlbumResponseSimpleApiDTO(
                    id = 1,
                    title = "Greatest Hits",
                    imageKey = "fake-album-img-001",
                    albumUrl = "https://fake.url/albums/1",
                    artists = listOf("Queen"),
                    release = LocalDate.of(1981, 11, 2)
                )
            ),
            artists = listOf(
                ArtistResponseSimpleApiDTO(
                    id = 1,
                    nameArtist = "Queen",
                    imageKey = "fake-artist-img-001",
                    artistUrl = "https://fake.url/artists/1"
                )
            ),
            status = HomeStatusApiDTO(
                songsLoaded = true,
                albumsLoaded = true,
                artistsLoaded = true,
                error = null
            )
        )
    }
}