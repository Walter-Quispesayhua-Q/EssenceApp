package com.essence.essenceapp.feature.song.data.api

import com.essence.essenceapp.feature.song.data.dto.SongResponseApiDTO
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SongApiService {

    companion object {
        const val BASE = "song"
    }

    @GET("$BASE/{songId}")
    suspend fun getSong(@Path("songId") songId: Long): SongResponseApiDTO?

    @POST("$BASE/{songId}/like")
    suspend fun addLikeSong(@Path("songId") songId: Long)

    @DELETE("$BASE/{songId}/like")
    suspend fun deleteLikeSong(@Path("songId") songId: Long)
}