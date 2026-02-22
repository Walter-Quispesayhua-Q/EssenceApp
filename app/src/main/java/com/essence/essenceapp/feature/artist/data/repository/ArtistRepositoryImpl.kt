package com.essence.essenceapp.feature.artist.data.repository

import com.essence.essenceapp.feature.artist.data.api.ArtistApiService
import com.essence.essenceapp.feature.artist.data.mapper.artistToDomain
import com.essence.essenceapp.feature.artist.domain.model.Artist
import com.essence.essenceapp.feature.artist.domain.repository.ArtistRepository

class ArtistRepositoryImpl(
    private val apiService: ArtistApiService
): ArtistRepository {

    override suspend fun getArtist(artistId: Long): Artist? {
        val apiDTO = apiService.getArtist(artistId)
        return apiDTO?.artistToDomain()
    }
}