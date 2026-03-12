package com.essence.essenceapp.feature.song.domain.usecase

import com.essence.essenceapp.feature.song.domain.model.Song
import com.essence.essenceapp.feature.song.domain.repository.SongRepository

class GetSongUseCase(
    private val songRepository: SongRepository
) {
    suspend operator fun invoke(songId: Long): Result<Song> {
        val response = songRepository.getSong(songId)
        return if (response != null) {
            Result.success(response)
        } else {
            Result.failure(Exception("Error al obtener cancion"))
        }
    }
}