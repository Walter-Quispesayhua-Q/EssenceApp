package com.essence.essenceapp.feature.playlist.domain.repository

import com.essence.essenceapp.feature.playlist.domain.model.Playlist
import com.essence.essenceapp.feature.playlist.domain.model.PlaylistRequest
import com.essence.essenceapp.feature.playlist.domain.model.PlaylistSimple
import com.essence.essenceapp.feature.playlist.domain.model.PlaylistsSimples
import com.essence.essenceapp.feature.song.domain.model.SongSimple

interface PlaylistRepository {

    //crud
    suspend fun createPlaylist(playlistRequest: PlaylistRequest): PlaylistSimple?
    suspend fun updatePlaylist(id: Long, playlistRequest: PlaylistRequest): PlaylistSimple?
    suspend fun getPlaylist(id: Long): Playlist?
    suspend fun getForUpdate(id: Long): Playlist?
    suspend fun deletePlaylist(id: Long)

    //manager content

    suspend fun getListSongs(id: Long): List<SongSimple>?
    suspend fun addSongToPlaylist(id: Long, songId: Long): Boolean
    suspend fun deleteSongOfPlaylist(id: Long, songId: Long)

    //lists
    suspend fun getPlaylistsByUser(userId: Long): PlaylistsSimples?

    suspend fun addLikePlaylist(id: Long)
    suspend fun deleteLikePlaylist(id: Long)
}