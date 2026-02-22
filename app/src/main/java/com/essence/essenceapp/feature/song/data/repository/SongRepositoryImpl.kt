package com.essence.essenceapp.feature.song.data.repository

import com.essence.essenceapp.feature.song.data.api.SongApiService
import com.essence.essenceapp.feature.song.data.mapper.songToDomain
import com.essence.essenceapp.feature.song.domain.model.Song
import com.essence.essenceapp.feature.song.domain.repository.SongRepository

class SongRepositoryImpl(
    private val apiService: SongApiService
): SongRepository {

    override suspend fun getSong(songId: Long): Song? {
        val apiDTO = apiService.getSong(songId)
        return apiDTO?.songToDomain()
    }
}