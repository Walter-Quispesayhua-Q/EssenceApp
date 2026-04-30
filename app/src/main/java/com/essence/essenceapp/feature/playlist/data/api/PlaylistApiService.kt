package com.essence.essenceapp.feature.playlist.data.api

import com.essence.essenceapp.feature.playlist.data.dto.PlaylistEditResponseApiDTO
import com.essence.essenceapp.feature.playlist.data.dto.PlaylistRequestApiDTO
import com.essence.essenceapp.feature.playlist.data.dto.PlaylistResponseApiDTO
import com.essence.essenceapp.feature.playlist.data.dto.PlaylistResponseSimpleApiDTO
import com.essence.essenceapp.feature.playlist.data.dto.PlaylistsSimplesResponseApiDTO
import com.essence.essenceapp.feature.song.data.dto.SongResponseSimpleApiDTO
import com.essence.essenceapp.shared.data.dto.ApiResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PlaylistApiService {
    companion object {
        const val BASE = "playlist"
    }

    @POST(BASE)
    suspend fun createPlaylist(
        @Body playlistRequestApiDto: PlaylistRequestApiDTO
    ): ApiResponseDto<PlaylistResponseSimpleApiDTO>?

    @PUT("$BASE/{id}")
    suspend fun updatePlaylist(
        @Path("id") id: Long,
        @Body playlistRequestApiDTO: PlaylistRequestApiDTO
    ): ApiResponseDto<PlaylistResponseSimpleApiDTO>?

    @GET("$BASE/{id}")
    suspend fun getPlaylist(@Path("id") id: Long): PlaylistResponseApiDTO?

    @GET("$BASE/{id}/edit")
    suspend fun getForUpdate(@Path("id") id: Long): PlaylistEditResponseApiDTO?

    @DELETE("$BASE/{id}")
    suspend fun deletePlaylist(@Path("id") id: Long)

    @GET("$BASE/{id}/songs")
    suspend fun getListSongs(@Path("id") id: Long): List<SongResponseSimpleApiDTO>?

    @POST("$BASE/{id}/songs/{songKey}")
    suspend fun addSongToPlaylist(
        @Path("id") id: Long,
        @Path("songKey") songKey: String
    ): Boolean

    @DELETE("$BASE/{id}/songs/{songId}")
    suspend fun deleteSongOfPlaylist(
        @Path("id") id: Long,
        @Path("songId") songId: Long
    )

    @GET(BASE)
    suspend fun getPlaylistsByUser(): PlaylistsSimplesResponseApiDTO?

    @POST("$BASE/{id}/like")
    suspend fun addLikePlaylist(@Path("id") id: Long)

    @DELETE("$BASE/{id}/like")
    suspend fun deleteLikePlaylist(@Path("id") id: Long)
}