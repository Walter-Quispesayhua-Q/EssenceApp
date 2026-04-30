package com.essence.essenceapp.feature.artist.data.repository

import com.essence.essenceapp.feature.artist.data.api.ArtistApiService
import com.essence.essenceapp.feature.artist.data.mapper.artistToDomain
import com.essence.essenceapp.feature.artist.domain.model.Artist
import com.essence.essenceapp.feature.artist.domain.repository.ArtistRepository
import com.essence.essenceapp.shared.cache.QueueCache

class ArtistRepositoryImpl(
    private val apiService: ArtistApiService,
    private val queueCache: QueueCache
) : ArtistRepository {

    override suspend fun getArtist(artistLookup: String): Artist? {
        val apiDTO = apiService.getArtist(artistLookup)
        val mapped = apiDTO?.artistToDomain()
        mapped?.songs?.let { queueCache.set("artist:$artistLookup", it) }
        return mapped
    }

    override suspend fun addLikeArtist(artistId: Long) {
        apiService.addLikeArtist(artistId)
    }

    override suspend fun deleteLikeArtist(artistId: Long) {
        apiService.deleteLikeArtist(artistId)
    }
}