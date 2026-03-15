package com.essence.essenceapp.feature.album.ui

import com.essence.essenceapp.feature.album.domain.model.Album

sealed interface AlbumDetailUiState {
    data object Loading : AlbumDetailUiState
    data class Error(val message: String) : AlbumDetailUiState
    data class Success(
        val album: Album,
        val isLikeSubmitting: Boolean = false
    ) : AlbumDetailUiState
}