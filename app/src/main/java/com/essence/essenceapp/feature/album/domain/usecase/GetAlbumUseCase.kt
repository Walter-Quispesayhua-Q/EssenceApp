package com.essence.essenceapp.feature.album.domain.usecase

import com.essence.essenceapp.feature.album.domain.model.Album
import com.essence.essenceapp.feature.album.domain.repository.AlbumRepository

class GetAlbumUseCase(private val albumRepository: AlbumRepository) {
    suspend operator fun invoke(albumId: Long): Result<Album> {
        val response = albumRepository.getAlbum(albumId)
        return if (response != null) {
            Result.success(response)
        } else {
            Result.failure(Exception("Error al obtener album"))
        }
    }
}