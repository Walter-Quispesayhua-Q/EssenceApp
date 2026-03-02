package com.essence.essenceapp.feature.history.data.repository

import com.essence.essenceapp.feature.history.data.api.HistoryApiService
import com.essence.essenceapp.feature.history.data.mapper.historyToDto
import com.essence.essenceapp.feature.history.domain.model.History
import com.essence.essenceapp.feature.history.domain.repository.HistoryRepository
import com.essence.essenceapp.feature.song.data.mapper.songToSimpleDomain
import com.essence.essenceapp.feature.song.domain.model.SongSimple

class HistoryRepositoryImpl(
    private val apiService: HistoryApiService
): HistoryRepository {

    override suspend fun addSongHistory(
        songId: Long,
        history: History
    ) {
        val request = history.historyToDto()
        apiService.addSongHistory(songId,request)
    }

    override suspend fun getSongsOfHistory(): List<SongSimple>? {
        val response = apiService.getSongsOfHistory()
        return response?.mapNotNull { it.songToSimpleDomain() }
    }
}