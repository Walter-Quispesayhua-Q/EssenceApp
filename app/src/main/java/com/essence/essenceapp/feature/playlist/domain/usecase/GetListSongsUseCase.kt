package com.essence.essenceapp.feature.playlist.domain.usecase

import com.essence.essenceapp.feature.playlist.domain.repository.PlaylistRepository
import com.essence.essenceapp.feature.song.domain.model.SongSimple

class GetListSongsUseCase(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(id: Long): Result<List<SongSimple>>{
        val response = playlistRepository.getListSongs(id)
        return if (response != null) {
            Result.success(response)
        } else {
            Result.failure(Exception("Error al obtener la lista de canciones"))
        }
    }
}