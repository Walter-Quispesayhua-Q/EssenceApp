package com.essence.essenceapp.feature.history.domain.usecase

import com.essence.essenceapp.feature.history.domain.repository.HistoryRepository
import com.essence.essenceapp.feature.song.domain.model.SongSimple

class GetSongsOfHistoryUseCase(
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke(): Result<List<SongSimple>> {
        val response = historyRepository.getSongsOfHistory()
        return if (response != null) {
            Result.success(response)
        } else {
            Result.failure(Exception("No se pudo obtener el historial"))
        }
    }
}