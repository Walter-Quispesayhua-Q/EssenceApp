package com.essence.essenceapp.feature.playlist.domain.usecase

import com.essence.essenceapp.feature.playlist.domain.model.Playlist
import com.essence.essenceapp.feature.playlist.domain.repository.PlaylistRepository

class GetForUpdateUseCase(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(id: Long): Result<Playlist>{
        val response = playlistRepository.getForUpdate(id)
        return if (response != null) {
            Result.success(response)
        } else {
            Result.failure(Exception("Error al obtener datos de playlist para editar"))
        }
    }
}