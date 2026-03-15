package com.essence.essenceapp.feature.album.domain.usecase

import com.essence.essenceapp.feature.album.domain.repository.AlbumRepository

class AddLikeAlbumUseCase(
    private val albumRepository: AlbumRepository
) {
    suspend operator fun invoke(albumId: Long): Result<Unit> {
        return try {
            albumRepository.addLikeAlbum(albumId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}