package com.essence.essenceapp.feature.song.data.api

import com.essence.essenceapp.feature.song.data.dto.SongResponseApiDTO
import retrofit2.http.GET
import retrofit2.http.Path

interface SongApiService {

    companion object {
        const val BASE = "song"
    }

    @GET("$BASE/{songId}")
    suspend fun getSong(@Path("songId") songId: Long): SongResponseApiDTO?

}