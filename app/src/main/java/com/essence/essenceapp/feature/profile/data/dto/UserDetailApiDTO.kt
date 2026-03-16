package com.essence.essenceapp.feature.profile.data.dto

import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class UserDetailApiDTO(
    val id: Long?,
    val username: String?,
    val email: String?,
    val createdAt: Instant?,
    val updatedAt: Instant?,
    val totalLikedSongs: Int?,
    val totalLikedAlbums: Int?,
    val totalLikedArtists: Int?,
    val totalPlaylists: Int?,
    val totalPlayHistory: Int?
)