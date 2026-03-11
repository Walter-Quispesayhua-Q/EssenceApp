package com.essence.essenceapp.feature.playlist.domain.usecase

import com.essence.essenceapp.feature.playlist.domain.model.PlaylistsSimples
import com.essence.essenceapp.feature.playlist.domain.repository.PlaylistRepository

class GetPlaylistsByUserUseCase(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(userId: Long): Result<PlaylistsSimples> {
        val response = playlistRepository.getPlaylistsByUser(userId)
        return if (response != null) {
            Result.success(response)
        } else {
            Result.failure(Exception("No se pudo obtener las playlists"))
        }
    }
}