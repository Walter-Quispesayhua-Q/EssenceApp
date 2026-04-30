package com.essence.essenceapp.feature.song.data.api

import com.essence.essenceapp.feature.song.data.dto.SongResponseApiDTO
import com.essence.essenceapp.feature.song.data.dto.SongSyncRequestApiDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface SongApiService {

    companion object {
        const val BASE = "song"
    }

    @GET("$BASE/{songLookup}")
    suspend fun getSong(
        @Path("songLookup") songLookup: String,
        @Query("forceRefresh") forceRefresh: Boolean = false
    ): SongResponseApiDTO?

    @POST("$BASE/sync")
    suspend fun syncSong(
        @Body request: SongSyncRequestApiDTO
    ): SongResponseApiDTO?

    @PATCH("$BASE/{videoId}/streaming-url")
    suspend fun refreshStreamingUrl(
        @Path("videoId") videoId: String,
        @Query("streamingUrl") streamingUrl: String
    ): Response<Unit>

    @POST("$BASE/{songId}/like")
    suspend fun addLikeSong(@Path("songId") songId: Long)

    @DELETE("$BASE/{songId}/like")
    suspend fun deleteLikeSong(@Path("songId") songId: Long)
}
