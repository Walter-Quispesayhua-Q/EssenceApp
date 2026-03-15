package com.essence.essenceapp.feature.album.domain.repository

import com.essence.essenceapp.feature.album.domain.model.Album

interface AlbumRepository {
    suspend fun getAlbum(albumId: Long): Album?
    suspend fun addLikeAlbum(albumId: Long)
    suspend fun deleteLikeAlbum(albumId: Long)
}