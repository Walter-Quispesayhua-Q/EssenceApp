package com.essence.essenceapp.feature.playlist.domain.model

/**
 * Modelo dedicado al formulario de edición de playlists.
 *
 * Contiene únicamente los campos editables/identificativos que el endpoint
 * `GET /playlist/{id}/edit` devuelve. No incluye metadata de presentación
 * (totalSongs, totalLikes, fechas, isLiked) ya que esos datos no aplican
 * a la edición y el backend puede no enviarlos.
 *
 * Esto evita el bug en el que el mapper genérico `playlistToDomain()` retornaba
 * null por requerir campos que el endpoint /edit no provee.
 */
data class PlaylistEditable(
    val id: Long,
    val title: String,
    val description: String?,
    val imageKey: String?,
    val isPublic: Boolean,
    val type: String = "NORMAL"
)
