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
import java.time.Instant

interface SongApiService {

    companion object {
        const val BASE = "song"
    }

    @GET("$BASE/{hlsMasterKey}")
    suspend fun getSong(
        @Path("hlsMasterKey") hlsMasterKey: String,
        @Query("forceRefresh") forceRefresh: Boolean = false
    ): SongResponseApiDTO?

    @POST("$BASE/sync")
    suspend fun syncSong(
        @Body request: SongSyncRequestApiDTO
    ): SongResponseApiDTO?

    @PATCH("$BASE/{hlsMasterKey}/streaming-url")
    suspend fun refreshStreamingUrl(
        @Path("hlsMasterKey") hlsMasterKey: String,
        @Query("streamingUrl") streamingUrl: String,
        @Query("expiresAt") expiresAt: Instant? = null
    ): Response<Unit>

    @POST("$BASE/{songId}/like")
    suspend fun addLikeSong(@Path("songId") songId: Long)

    @DELETE("$BASE/{songId}/like")
    suspend fun deleteLikeSong(@Path("songId") songId: Long)
}
