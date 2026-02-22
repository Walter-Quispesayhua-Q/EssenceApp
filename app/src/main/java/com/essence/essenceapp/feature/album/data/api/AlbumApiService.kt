package com.essence.essenceapp.feature.album.data.api

import com.essence.essenceapp.feature.album.data.dto.AlbumResponseApiDTO
import retrofit2.http.GET
import retrofit2.http.Path

interface AlbumApiService {

    companion object {
        const val BASE = "album"
    }
    @GET("$BASE/{albumId}")
    suspend fun getAlbum(@Path("albumId") albumId: Long): AlbumResponseApiDTO?
}