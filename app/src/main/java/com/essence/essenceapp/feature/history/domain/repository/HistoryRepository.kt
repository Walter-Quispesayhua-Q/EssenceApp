package com.essence.essenceapp.feature.history.domain.repository

import com.essence.essenceapp.feature.history.domain.model.History
import com.essence.essenceapp.feature.song.domain.model.SongSimple

interface HistoryRepository {
    suspend fun addSongHistory(songId: Long, history: History)
    suspend fun getSongsOfHistory(limit: Int? = null): List<SongSimple>?
}