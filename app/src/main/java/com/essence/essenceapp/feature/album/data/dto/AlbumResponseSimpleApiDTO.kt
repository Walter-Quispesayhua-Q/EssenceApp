package com.essence.essenceapp.feature.album.data.dto

import java.time.LocalDate

data class AlbumResponseSimpleApiDTO(
    val id: Long?,
    val title: String?,
    val imageKey: String?,
    val albumUrl: String?,
    val artists: List<String>?,
    val release: LocalDate?
)
