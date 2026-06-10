package com.essence.essenceapp.feature.song.data.repository

import android.util.Log
import com.essence.essenceapp.feature.song.domain.model.Song
import com.essence.essenceapp.feature.song.domain.resolver.SongResolveResult

/**
 * Adapta el resultado del resolver nuevo al contrato actual del repositorio.
 *
 * El resolver conserva la causa exacta del fallo, pero SongRepository todavia
 * devuelve Song?. Este mapper deja pasar la cancion cuando todo sale bien y
 * registra el error antes de devolver null.
 */
internal fun SongResolveResult.toSongOrNull(
    tag: String,
    operation: String
): Song? = when (this) {
    is SongResolveResult.Success -> song
    is SongResolveResult.Failure -> {
        Log.w(tag, "$operation failed: ${error.message}")
        null
    }
}