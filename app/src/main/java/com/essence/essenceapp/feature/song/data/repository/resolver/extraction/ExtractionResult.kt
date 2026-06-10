package com.essence.essenceapp.feature.song.data.repository.resolver.extraction

/**
 * Resultado interno del extractor de NewPipe.
 */
sealed interface ExtractionResult {

    data class Success(val data: ExtractedSongData) : ExtractionResult

    data object Empty : ExtractionResult

    data object Incomplete : ExtractionResult
}
