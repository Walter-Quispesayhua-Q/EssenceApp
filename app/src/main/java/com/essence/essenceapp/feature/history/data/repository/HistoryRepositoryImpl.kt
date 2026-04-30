package com.essence.essenceapp.feature.history.data.repository

import com.essence.essenceapp.feature.history.data.api.HistoryApiService
import com.essence.essenceapp.feature.history.data.mapper.historyToDto
import com.essence.essenceapp.feature.history.domain.model.History
import com.essence.essenceapp.feature.history.domain.repository.HistoryRepository
import com.essence.essenceapp.feature.song.data.mapper.songToSimpleDomain
import com.essence.essenceapp.feature.song.domain.model.SongSimple
import com.essence.essenceapp.shared.cache.QueueCache

class HistoryRepositoryImpl(
    private val apiService: HistoryApiService,
    private val queueCache: QueueCache
): HistoryRepository {

    override suspend fun addSongHistory(
        songId: Long,
        history: History
    ) {
        val request = history.historyToDto()
        apiService.addSongHistory(songId,request)
    }

    override suspend fun getSongsOfHistory(limit: Int?): List<SongSimple>? {
        val response = apiService.getSongsOfHistory(limit)
        val mapped = response?.mapNotNull { it.songToSimpleDomain() }
        if (limit == null) {
            mapped?.let { queueCache.set("history", it) }
        }
        return mapped
    }
}
