package com.essence.essenceapp.feature.song.data.repository

import com.essence.essenceapp.feature.song.data.dto.SongResponseApiDTO
import com.essence.essenceapp.feature.song.model.Song

class SongRepositoryImpl: SongRepository {
    override suspend fun getSong(data: SongResponseApiDTO): Song {
        TODO("Not yet implemented")
    }

    override suspend fun getSongs(data: List<SongResponseApiDTO>): List<Song> {
        TODO("Not yet implemented")
    }

}