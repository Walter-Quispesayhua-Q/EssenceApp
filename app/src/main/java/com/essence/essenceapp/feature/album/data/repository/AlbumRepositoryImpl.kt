package com.essence.essenceapp.feature.album.data.repository

import com.essence.essenceapp.feature.album.data.api.AlbumApiService
import com.essence.essenceapp.feature.album.data.mapper.albumToDomain
import com.essence.essenceapp.feature.album.domain.model.Album
import com.essence.essenceapp.feature.album.domain.repository.AlbumRepository
import com.essence.essenceapp.shared.cache.QueueCache

class AlbumRepositoryImpl(
    private val apiService: AlbumApiService,
    private val queueCache: QueueCache
) : AlbumRepository {

    override suspend fun getAlbum(albumLookup: String): Album? {
        val apiDTO = apiService.getAlbum(albumLookup)
        val mapped = apiDTO?.albumToDomain()
        mapped?.songs?.let { queueCache.set("album:$albumLookup", it) }
        return mapped
    }

    override suspend fun addLikeAlbum(albumId: Long) {
        apiService.addLikeAlbum(albumId)
    }

    override suspend fun deleteLikeAlbum(albumId: Long) {
        apiService.deleteLikeAlbum(albumId)
    }
}