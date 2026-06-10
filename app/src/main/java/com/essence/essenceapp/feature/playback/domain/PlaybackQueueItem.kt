package com.essence.essenceapp.feature.playback.domain

/**
 * Canción preparada dentro de una cola de reproducción.
 *
 * Contiene los datos mínimos para identificarla, mostrarla en la UI y pedir
 * su versión reproducible cuando llegue el momento de sonar.
 */
data class PlaybackQueueItem(
    val hlsMasterKey: String,
    val songId: Long? = null,
    val title: String,
    val artistName: String,
    val imageKey: String?,
    val durationMs: Long
)