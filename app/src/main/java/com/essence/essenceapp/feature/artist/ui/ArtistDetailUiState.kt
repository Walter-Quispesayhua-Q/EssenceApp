package com.essence.essenceapp.feature.artist.ui

import com.essence.essenceapp.feature.artist.domain.model.Artist

sealed interface ArtistDetailUiState {
    data object Loading : ArtistDetailUiState
    data class Error(val message: String) : ArtistDetailUiState
    data class Success(
        val artist: Artist,
        val isLikeSubmitting: Boolean = false
    ) : ArtistDetailUiState}