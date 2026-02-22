package com.essence.essenceapp.feature.album.domain.model

import java.time.LocalDate

data class AlbumSimple(
    val id: Long,
    val title: String,
    val imageKey: String?,
    val albumUrl: String,
    val artists: List<String>?,
    val release: LocalDate?
)
