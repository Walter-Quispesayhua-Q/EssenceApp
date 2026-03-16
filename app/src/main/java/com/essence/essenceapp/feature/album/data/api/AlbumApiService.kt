package com.essence.essenceapp.feature.album.data.api

import com.essence.essenceapp.feature.album.data.dto.AlbumResponseApiDTO
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AlbumApiService {

    companion object {
        const val BASE = "album"
    }

    @GET("$BASE/{albumLookup}")
    suspend fun getAlbum(@Path("albumLookup") albumLookup: String): AlbumResponseApiDTO?

    @POST("$BASE/{albumId}/like")
    suspend fun addLikeAlbum(@Path("albumId") albumId: Long)

    @DELETE("$BASE/{albumId}/like")
    suspend fun deleteLikeAlbum(@Path("albumId") albumId: Long)
}