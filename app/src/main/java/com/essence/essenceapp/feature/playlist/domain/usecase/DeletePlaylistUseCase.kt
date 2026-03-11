package com.essence.essenceapp.feature.playlist.domain.usecase

import com.essence.essenceapp.feature.playlist.domain.repository.PlaylistRepository

class DeletePlaylistUseCase(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(id: Long): Result<Unit>{
        return try {
            playlistRepository.deletePlaylist(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error al eliminar playlist"))
        }
    }
}