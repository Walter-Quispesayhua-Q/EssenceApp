package com.essence.essenceapp.feature.song.domain.resolver

import com.essence.essenceapp.feature.song.domain.model.Song

/**
 * Resultado de intentar preparar una cancion reproducible.
 *
 * Cuando termina bien, contiene la Song final que ya puede usar playback.
 * Cuando falla, contiene un SongResolveError con la causa principal.
 */
sealed interface SongResolveResult {

    data class Success(val song: Song) : SongResolveResult

    data class Failure(val error: SongResolveError) : SongResolveResult
}
