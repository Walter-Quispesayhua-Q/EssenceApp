package com.essence.essenceapp.feature.song.domain.repository

import com.essence.essenceapp.feature.song.domain.model.Song

interface SongRepository {
    suspend fun getSong(hlsMasterKey: String, forceRefresh: Boolean = false): Song?

    suspend fun syncSong(hlsMasterKey: String): Song?

    suspend fun refreshStreamingUrl(hlsMasterKey: String, songId: Long): Song?

    suspend fun addLikeSong(songId: Long)

    suspend fun deleteLikeSong(songId: Long)
}