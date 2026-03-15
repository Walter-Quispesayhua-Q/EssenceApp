package com.essence.essenceapp.feature.playlist.data.repository

import com.essence.essenceapp.feature.playlist.data.api.PlaylistApiService
import com.essence.essenceapp.feature.playlist.data.mapper.playlistRequestToData
import com.essence.essenceapp.feature.playlist.data.mapper.playlistToDomain
import com.essence.essenceapp.feature.playlist.data.mapper.playlistToSimpleDomain
import com.essence.essenceapp.feature.playlist.domain.model.Playlist
import com.essence.essenceapp.feature.playlist.domain.model.PlaylistRequest
import com.essence.essenceapp.feature.playlist.domain.model.PlaylistSimple
import com.essence.essenceapp.feature.playlist.domain.model.PlaylistsSimples
import com.essence.essenceapp.feature.playlist.domain.repository.PlaylistRepository
import com.essence.essenceapp.feature.song.data.mapper.songToSimpleDomain
import com.essence.essenceapp.feature.song.domain.model.SongSimple
import com.essence.essenceapp.feature.playlist.data.mapper.playlistsSimplesToDomain

class PlaylistRepositoryImpl(
    private val apiService: PlaylistApiService
): PlaylistRepository {

    //crud

    override suspend fun createPlaylist(playlistRequest: PlaylistRequest): PlaylistSimple? {
        val request = playlistRequest.playlistRequestToData()
        val response = apiService.createPlaylist(request)
        return response?.data?.playlistToSimpleDomain()
    }

    override suspend fun updatePlaylist(
        id: Long,
        playlistRequest: PlaylistRequest
    ): PlaylistSimple? {
        val request = playlistRequest.playlistRequestToData()
        val response = apiService.updatePlaylist(id, request)
        return response?.data?.playlistToSimpleDomain()
    }

    override suspend fun getPlaylist(id: Long): Playlist? {
        val response = apiService.getPlaylist(id)
        return response?.playlistToDomain()
    }

    override suspend fun getForUpdate(id: Long): Playlist? {
        val response = apiService.getForUpdate(id)
        return response?.playlistToDomain()
    }

    override suspend fun deletePlaylist(id: Long) {
        apiService.deletePlaylist(id)
    }

    //manager content

    override suspend fun getListSongs(id: Long): List<SongSimple>? {
        val response = apiService.getListSongs(id)
        return response?.mapNotNull {
            it.songToSimpleDomain()
        }
    }

    override suspend fun addSongToPlaylist(id: Long, songId: Long): Boolean {
        return apiService.addSongToPlaylist(id, songId)
    }

    override suspend fun deleteSongOfPlaylist(id: Long, songId: Long){
        apiService.deleteSongOfPlaylist(id, songId)
    }

    //lists

    override suspend fun getPlaylistsByUser(userId: Long): PlaylistsSimples? {
        val response = apiService.getPlaylistsByUser(userId)
        return response?.playlistsSimplesToDomain()
    }

    override suspend fun addLikePlaylist(id: Long) {
        apiService.addLikePlaylist(id)
    }

    override suspend fun deleteLikePlaylist(id: Long) {
        apiService.deleteLikePlaylist(id)
    }
}