package com.essence.essenceapp.feature.artist.data.api

import com.essence.essenceapp.feature.artist.data.dto.ArtistResponseApiDTO
import retrofit2.http.GET
import retrofit2.http.Path

interface ArtistApiService {
    companion object {
        const val BASE = "artist"
    }

    @GET("$BASE/{artistId}")
    suspend fun getArtist(@Path("artistId") artistId: Long): ArtistResponseApiDTO?
}