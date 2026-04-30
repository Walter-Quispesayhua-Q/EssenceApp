package com.essence.essenceapp.feature.history.domain.usecase

import com.essence.essenceapp.feature.history.domain.model.History
import com.essence.essenceapp.feature.history.domain.repository.HistoryRepository
import kotlinx.coroutines.CancellationException

class AddSongHistoryUseCase(
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke(songId: Long, history: History): Result<Unit> {
        return try {
            historyRepository.addSongHistory(songId, history)
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
