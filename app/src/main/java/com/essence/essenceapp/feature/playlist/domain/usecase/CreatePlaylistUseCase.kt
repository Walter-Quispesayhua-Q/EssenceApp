package com.essence.essenceapp.feature.playlist.domain.usecase

import com.essence.essenceapp.feature.playlist.domain.model.PlaylistRequest
import com.essence.essenceapp.feature.playlist.domain.model.PlaylistSimple
import com.essence.essenceapp.feature.playlist.domain.repository.PlaylistRepository

class CreatePlaylistUseCase(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(playlistRequest: PlaylistRequest): Result<PlaylistSimple>{
        if (playlistRequest.title.isBlank()) {
            return Result.failure(Exception("El titulo no puede estar vacio"))
        }

        val response = playlistRepository.createPlaylist(playlistRequest)

        return if (response != null) {
            Result.success(response)
        } else {
            Result.failure(Exception
                ("Error al crear una Playlist"))
        }
    }
}