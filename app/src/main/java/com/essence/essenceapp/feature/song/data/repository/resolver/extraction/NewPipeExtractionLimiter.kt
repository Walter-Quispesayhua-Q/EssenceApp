package com.essence.essenceapp.feature.song.data.repository.resolver.extraction

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

/**
 * Limita la cantidad de extracciones NewPipe simultaneas en toda la app.
 *
 * NewPipe consulta YouTube parseando paginas y respuestas Innertube. Si
 * lanzamos demasiadas extracciones a la vez (current + prefetch + recovery),
 * YouTube puede responder con audioStreams vacios, captchas o rate limit.
 *
 * Permite hasta MAX_CONCURRENT extracciones a la vez. Las demas hacen cola
 * hasta que se libere un permit. El hedging interno (primary + backup) consume
 * los dos permits de una sola extraccion: una sola cancion puede ocupar el
 * limiter por completo durante su pasada.
 */
@Singleton
class NewPipeExtractionLimiter @Inject constructor() {
    private val semaphore = Semaphore(MAX_CONCURRENT)

    val availablePermits: Int
        get() = semaphore.availablePermits

    suspend fun <T> withPermit(block: suspend () -> T): T = semaphore.withPermit { block() }

    private companion object {
        const val MAX_CONCURRENT = 2
    }
}
