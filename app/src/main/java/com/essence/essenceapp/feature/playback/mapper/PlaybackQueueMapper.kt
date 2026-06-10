package com.essence.essenceapp.feature.playback.mapper

import com.essence.essenceapp.feature.playback.domain.PlaybackQueueItem
import com.essence.essenceapp.feature.song.domain.model.Song
import com.essence.essenceapp.feature.song.domain.model.SongSimple

/**
 * Convierte canciones simples en items de cola para playback.
 *
 * Las pantallas usan SongSimple para listar canciones, pero el reproductor
 * necesita PlaybackQueueItem para abrir una cola.
 */
fun SongSimple.toQueueItem(): PlaybackQueueItem =
    PlaybackQueueItem(
        hlsMasterKey = hlsMasterKey,
        songId = id,
        title = title,
        artistName = artistName,
        imageKey = imageKey,
        durationMs = durationMs.toLong()
    )

fun List<SongSimple>.toQueueItems(): List<PlaybackQueueItem> =
    map { it.toQueueItem() }

fun Song.toQueueItem(): PlaybackQueueItem =
    PlaybackQueueItem(
        hlsMasterKey = hlsMasterKey,
        songId = id,
        title = title,
        artistName = artists.joinToString(", ") { it.nameArtist }
            .ifBlank { "Artista desconocido" },
        imageKey = imageKey,
        durationMs = durationMs.toLong()
    )