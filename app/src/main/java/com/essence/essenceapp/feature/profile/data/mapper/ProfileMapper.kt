package com.essence.essenceapp.feature.profile.data.mapper

import com.essence.essenceapp.feature.profile.data.dto.UserDetailApiDTO
import com.essence.essenceapp.feature.profile.domain.model.UserProfile

fun UserDetailApiDTO.userProfileToDomain(): UserProfile? {
    return UserProfile(
        id = this.id ?: return null,
        username = this.username ?: return null,
        email = this.email ?: return null,
        createdAt = this.createdAt ?: return null,
        updatedAt = this.updatedAt,
        totalLikedSongs = this.totalLikedSongs ?: 0,
        totalLikedAlbums = this.totalLikedAlbums ?: 0,
        totalLikedArtists = this.totalLikedArtists ?: 0,
        totalPlaylists = this.totalPlaylists ?: 0,
        totalPlayHistory = this.totalPlayHistory ?: 0
    )
}