package com.essence.essenceapp.feature.album.data.repository

import com.essence.essenceapp.feature.album.data.api.AlbumApiService
import com.essence.essenceapp.feature.album.data.mapper.albumToDomain
import com.essence.essenceapp.feature.album.domain.model.Album
import com.essence.essenceapp.feature.album.domain.repository.AlbumRepository

class AlbumRepositoryImpl(
    private val apiService: AlbumApiService
) : AlbumRepository {

    override suspend fun getAlbum(albumLookup: String): Album? {
        val apiDTO = apiService.getAlbum(albumLookup)
        return apiDTO?.albumToDomain()
    }

    override suspend fun addLikeAlbum(albumId: Long) {
        apiService.addLikeAlbum(albumId)
    }

    override suspend fun deleteLikeAlbum(albumId: Long) {
        apiService.deleteLikeAlbum(albumId)
    }
}