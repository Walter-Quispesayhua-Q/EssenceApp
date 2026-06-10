package com.essence.essenceapp.feature.playback.manager.resolver

import com.essence.essenceapp.feature.playback.domain.PlaybackQueueItem
import com.essence.essenceapp.feature.song.domain.model.Song

/**
 * Resuelve el item de la cola que playback quiere reproducir.
 *
 * Su trabajo es convertir un PlaybackQueueItem en una Song lista para sonar,
 * usando el flujo correcto según los datos disponibles del item.
 */
interface PlaybackSongResolver {
    suspend fun resolve(item: PlaybackQueueItem): Result<Song>
}