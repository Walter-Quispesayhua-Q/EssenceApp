package com.essence.essenceapp.feature.song.domain.repository

import com.essence.essenceapp.feature.song.domain.model.Song

interface SongRepository {
    suspend fun getSong(songLookup: String, forceRefresh: Boolean = false): Song?

    suspend fun syncSong(videoId: String): Song?

    suspend fun refreshStreamingUrl(
        currentSong: Song,
        isStillCurrent: () -> Boolean
    ): Song?
    suspend fun addLikeSong(songId: Long)
    suspend fun deleteLikeSong(songId: Long)
}