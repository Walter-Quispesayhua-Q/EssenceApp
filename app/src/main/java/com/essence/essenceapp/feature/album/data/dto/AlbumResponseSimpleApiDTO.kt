package com.essence.essenceapp.feature.album.data.dto

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class AlbumResponseSimpleApiDTO(
    val id: Long?,
    val title: String?,
    val imageKey: String?,
    val albumUrl: String?,
    val artists: List<String>?,
    val release: LocalDate?
)
