package com.essence.essenceapp.feature.song.data.repository

import com.essence.essenceapp.feature.song.data.dto.SongResponseApiDTO
import com.essence.essenceapp.feature.song.model.Song

interface SongRepository {
    suspend fun getSong(data: SongResponseApiDTO): Song
    suspend fun getSongs(data: List<SongResponseApiDTO>): List<Song>
}