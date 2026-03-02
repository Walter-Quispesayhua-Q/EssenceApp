package com.essence.essenceapp.feature.history.data.api

import com.essence.essenceapp.feature.history.data.dto.HistoryApiDTO
import com.essence.essenceapp.feature.song.data.dto.SongResponseSimpleApiDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface HistoryApiService {

    companion object {
        const val BASE = "history"
    }

    @POST("$BASE/songs/{songId}")
    suspend fun addSongHistory(
        @Path("songId") songId: Long, @Body historyApiDTO: HistoryApiDTO)

    @GET(BASE)
    suspend fun getSongsOfHistory(): List<SongResponseSimpleApiDTO>?
}