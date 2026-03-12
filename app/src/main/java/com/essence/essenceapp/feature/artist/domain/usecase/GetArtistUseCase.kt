package com.essence.essenceapp.feature.artist.domain.usecase

import com.essence.essenceapp.feature.artist.domain.model.Artist
import com.essence.essenceapp.feature.artist.domain.repository.ArtistRepository

class GetArtistUseCase(
    private val artistRepository: ArtistRepository
) {
    suspend operator fun invoke(artistId: Long): Result<Artist> {
        val response = artistRepository.getArtist(artistId)
        return if (response != null) {
            Result.success(response)
        } else {
            Result.failure(Exception("Error al obtener artista"))
        }
    }
}