package com.essence.essenceapp.feature.playlist.data.repository

import com.essence.essenceapp.feature.playlist.data.api.PlaylistApiService
import com.essence.essenceapp.feature.playlist.data.mapper.playlistRequestToData
import com.essence.essenceapp.feature.playlist.data.mapper.playlistToDomain
import com.essence.essenceapp.feature.playlist.data.mapper.playlistToSimpleDomain
import com.essence.essenceapp.feature.playlist.data.mapper.toEditableDomain
import com.essence.essenceapp.feature.playlist.domain.model.Playlist
import com.essence.essenceapp.feature.playlist.domain.model.PlaylistEditable
import com.essence.essenceapp.feature.playlist.domain.model.PlaylistRequest
import com.essence.essenceapp.feature.playlist.domain.model.PlaylistSimple
import com.essence.essenceapp.feature.playlist.domain.model.PlaylistsSimples
import com.essence.essenceapp.feature.playlist.domain.repository.PlaylistRepository
import com.essence.essenceapp.feature.song.data.mapper.songToSimpleDomain
import com.essence.essenceapp.feature.song.domain.model.SongSimple
import com.essence.essenceapp.feature.playlist.data.mapper.playlistsSimplesToDomain
import com.essence.essenceapp.shared.cache.QueueCache

class PlaylistRepositoryImpl(
    private val apiService: PlaylistApiService,
    private val queueCache: QueueCache
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

    override suspend fun getForUpdate(id: Long): PlaylistEditable? {
        val response = apiService.getForUpdate(id)
        return response?.toEditableDomain()
    }

    override suspend fun deletePlaylist(id: Long) {
        apiService.deletePlaylist(id)
    }

    //manager content

    override suspend fun getListSongs(id: Long): List<SongSimple>? {
        val response = apiService.getListSongs(id)
        val mapped = response?.mapNotNull {
            it.songToSimpleDomain()
        }
        mapped?.let { queueCache.set("playlist:$id", it) }
        return mapped
    }

    override suspend fun addSongToPlaylist(id: Long, songKey: String): Boolean {
        return apiService.addSongToPlaylist(id, songKey)
    }

    override suspend fun deleteSongOfPlaylist(id: Long, songId: Long){
        apiService.deleteSongOfPlaylist(id, songId)
    }

    //lists

    override suspend fun getPlaylistsByUser(): PlaylistsSimples? {
        val response = apiService.getPlaylistsByUser()
        return response?.playlistsSimplesToDomain()
    }

    override suspend fun addLikePlaylist(id: Long) {
        apiService.addLikePlaylist(id)
    }

    override suspend fun deleteLikePlaylist(id: Long) {
        apiService.deleteLikePlaylist(id)
    }
}