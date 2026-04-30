package com.essence.essenceapp.feature.playlist.domain.usecase

import com.essence.essenceapp.feature.playlist.domain.model.PlaylistEditable
import com.essence.essenceapp.feature.playlist.domain.repository.PlaylistRepository
import kotlinx.coroutines.CancellationException

class GetForUpdateUseCase(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(id: Long): Result<PlaylistEditable> {
        return try {
            val response = playlistRepository.getForUpdate(id)
            if (response != null) {
                Result.success(response)
            } else {
                Result.failure(Exception("Error al obtener datos de playlist para editar"))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}