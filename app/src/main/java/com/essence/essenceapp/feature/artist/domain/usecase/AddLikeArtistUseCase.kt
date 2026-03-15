package com.essence.essenceapp.feature.artist.domain.usecase

import com.essence.essenceapp.feature.artist.domain.repository.ArtistRepository

class AddLikeArtistUseCase(
    private val artistRepository: ArtistRepository
) {
    suspend operator fun invoke(artistId: Long): Result<Unit> {
        return try {
            artistRepository.addLikeArtist(artistId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}