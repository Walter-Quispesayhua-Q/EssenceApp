package com.essence.essenceapp.feature.artist.domain.usecase

import com.essence.essenceapp.feature.artist.domain.model.Artist
import com.essence.essenceapp.feature.artist.domain.repository.ArtistRepository

class GetArtistUseCase(
    private val artistRepository: ArtistRepository
) {
    suspend operator fun invoke(artistLookup: String): Result<Artist> {
        val response = artistRepository.getArtist(artistLookup)
        return if (response != null) {
            Result.success(response)
        } else {
            Result.failure(Exception("Error al obtener artista"))
        }
    }
}