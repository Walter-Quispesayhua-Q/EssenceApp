package com.essence.essenceapp.feature.playlist.domain.usecase

import com.essence.essenceapp.feature.playlist.domain.repository.PlaylistRepository

class AddSongToPlaylistUseCase(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(id: Long, songId: Long): Result<Boolean> {
        val response = playlistRepository.addSongToPlaylist(id, songId)
        return if (response) {
            Result.success(response)
        } else {
            Result.failure(Exception("No se agregar la cancion a la playlist"))
        }
    }
}