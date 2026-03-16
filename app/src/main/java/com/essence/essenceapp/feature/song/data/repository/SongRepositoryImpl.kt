package com.essence.essenceapp.feature.song.data.repository

import com.essence.essenceapp.feature.song.data.api.SongApiService
import com.essence.essenceapp.feature.song.data.mapper.songToDomain
import com.essence.essenceapp.feature.song.domain.model.Song
import com.essence.essenceapp.feature.song.domain.repository.SongRepository

class SongRepositoryImpl(
    private val apiService: SongApiService
) : SongRepository {

    override suspend fun getSong(songLookup: String): Song? {
        val apiDTO = apiService.getSong(songLookup)
        return apiDTO?.songToDomain()
    }

    override suspend fun addLikeSong(songId: Long) {
        apiService.addLikeSong(songId)
    }

    override suspend fun deleteLikeSong(songId: Long) {
        apiService.deleteLikeSong(songId)
    }
}