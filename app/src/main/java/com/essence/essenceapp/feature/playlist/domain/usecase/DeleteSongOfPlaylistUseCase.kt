package com.essence.essenceapp.feature.playlist.domain.usecase

import com.essence.essenceapp.feature.playlist.domain.repository.PlaylistRepository

class DeleteSongOfPlaylistUseCase(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(id: Long, songId: Long): Result<Unit> {
        return try {
            playlistRepository.deleteSongOfPlaylist(id, songId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error al quitar cancion de la playlist"))
        }
    }
}