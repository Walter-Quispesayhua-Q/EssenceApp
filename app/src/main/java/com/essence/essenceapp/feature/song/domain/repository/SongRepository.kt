package com.essence.essenceapp.feature.song.domain.repository

import com.essence.essenceapp.feature.song.domain.model.Song

interface SongRepository {
    suspend fun getSong(songId: Long): Song?
    suspend fun addLikeSong(songId: Long)
    suspend fun deleteLikeSong(songId: Long)
}