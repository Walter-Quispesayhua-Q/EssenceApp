package com.essence.essenceapp.feature.playlist.domain.usecase

import com.essence.essenceapp.feature.playlist.domain.model.PlaylistRequest
import com.essence.essenceapp.feature.playlist.domain.model.PlaylistSimple
import com.essence.essenceapp.feature.playlist.domain.repository.PlaylistRepository

class UpdatePlaylistUseCase(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(id: Long, playlistRequest: PlaylistRequest): Result<PlaylistSimple> {
        if (!playlistRequest.hasChanges) {
            return Result.failure(Exception("No actualizaste ningún campo"))
        }

        return try {
            val response = playlistRepository.updatePlaylist(id, playlistRequest)
            if (response != null) {
                Result.success(response)
            } else {
                Result.failure(Exception("Error al actualizar playlist"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}