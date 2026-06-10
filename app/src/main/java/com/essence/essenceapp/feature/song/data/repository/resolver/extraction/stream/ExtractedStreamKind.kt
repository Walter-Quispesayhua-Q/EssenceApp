package com.essence.essenceapp.feature.song.data.repository.resolver.extraction.stream

/**
 * Tipo de stream reproducible elegido desde la respuesta de NewPipe.
 *
 * Permite distinguir el camino principal de audio-only de los fallbacks sin
 * mezclar esa decision con el modelo final de Song.
 */
enum class ExtractedStreamKind {
    AUDIO_ONLY,
    PROGRESSIVE,
    HLS
}