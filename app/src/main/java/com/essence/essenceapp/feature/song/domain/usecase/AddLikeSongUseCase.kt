package com.essence.essenceapp.feature.song.domain.usecase

import com.essence.essenceapp.feature.song.domain.repository.SongRepository

class AddLikeSongUseCase(
    private val songRepository: SongRepository
) {
    suspend operator fun invoke(songId: Long): Result<Unit> {
        return try {
            songRepository.addLikeSong(songId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}