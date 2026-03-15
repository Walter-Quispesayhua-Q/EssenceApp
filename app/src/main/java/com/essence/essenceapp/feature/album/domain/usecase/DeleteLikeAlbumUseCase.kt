package com.essence.essenceapp.feature.album.domain.usecase

import com.essence.essenceapp.feature.album.domain.repository.AlbumRepository

class DeleteLikeAlbumUseCase(
    private val albumRepository: AlbumRepository
) {
    suspend operator fun invoke(albumId: Long): Result<Unit> {
        return try {
            albumRepository.deleteLikeAlbum(albumId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}