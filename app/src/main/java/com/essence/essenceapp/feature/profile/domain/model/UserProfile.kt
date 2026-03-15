package com.essence.essenceapp.feature.profile.domain.model

import java.time.Instant

data class UserProfile(
    val id: Long,
    val username: String,
    val email: String,
    val createdAt: Instant,
    val updatedAt: Instant?,
    val totalLikedSongs: Int,
    val totalLikedAlbums: Int,
    val totalLikedArtists: Int,
    val totalPlaylists: Int,
    val totalPlayHistory: Int
)