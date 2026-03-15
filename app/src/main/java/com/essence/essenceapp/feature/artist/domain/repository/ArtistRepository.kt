package com.essence.essenceapp.feature.artist.domain.repository

import com.essence.essenceapp.feature.artist.domain.model.Artist

interface ArtistRepository {
    suspend fun getArtist(artistId: Long): Artist?
    suspend fun addLikeArtist(artistId: Long)
    suspend fun deleteLikeArtist(artistId: Long)
}