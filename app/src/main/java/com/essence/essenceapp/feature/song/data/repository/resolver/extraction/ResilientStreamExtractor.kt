package com.essence.essenceapp.feature.song.data.repository.resolver.extraction

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.milliseconds

/**
 * Extrae una cancion desde NewPipe con tolerancia a errores transitorios.
 *
 * Estrategia:
 *  1. Hedged extraction: lanza dos intentos en paralelo separados por
 *     un breve delay; se queda con el primero que devuelva Success y
 *     cancela al otro. Si el primer intento termina con Empty o
 *     Incomplete antes del delay, el segundo igual corre para tener
 *     una segunda oportunidad real.
 *  2. Si la primera pasada no da Success, espera un backoff y hace una
 *     segunda pasada hedged. El backoff es mayor cuando el resultado
 *     fue Empty (puede ser una respuesta transitoria de YouTube con
 *     cero streams) que cuando fue Incomplete (suele resolverse rapido).
 *
 * Nunca convierte excepciones en Success: si todo falla, devuelve
 * Empty o Incomplete.
 */
@Singleton
class ResilientStreamExtractor @Inject constructor(
    private val pageFetcher: NewPipeStreamPageFetcher,
    private val reader: StreamExtractorReader,
    private val limiter: NewPipeExtractionLimiter
) {

    private val recentEmpties: ArrayDeque<Long> = ArrayDeque()
    private val recentEmptiesLock = Any()

    suspend fun extract(hlsMasterKey: String): ExtractionResult {
        Log.d(TAG, "extract[$hlsMasterKey] start")
        val startMs = System.currentTimeMillis()

        val first = hedgedExtraction(hlsMasterKey, pass = 1)
        if (first is ExtractionResult.Success) {
            Log.d(
                TAG,
                "extract[$hlsMasterKey] DONE pass=1 -> Success " +
                        "in ${System.currentTimeMillis() - startMs}ms"
            )
            return first
        }
        if (first is ExtractionResult.Empty) {
            val backoff = nextEmptyBackoff()
            Log.w(
                TAG,
                "extract[$hlsMasterKey] pass=1 -> Empty, " +
                        "confirming after ${backoff.delayMs}ms " +
                        "(recent_empties=${backoff.recentCount})"
            )
            delay(backoff.delayMs.milliseconds)
            val second = hedgedExtraction(hlsMasterKey, pass = 2)
            Log.d(
                TAG,
                "extract[$hlsMasterKey] DONE pass=2 after Empty -> ${second.summary()} " +
                        "in ${System.currentTimeMillis() - startMs}ms"
            )
            return second
        }

        Log.w(
            TAG,
            "extract[$hlsMasterKey] pass=1 -> Incomplete, " +
                    "retrying after ${RETRY_BACKOFF_MS}ms"
        )
        delay(RETRY_BACKOFF_MS.milliseconds)
        val second = hedgedExtraction(hlsMasterKey, pass = 2)
        Log.d(
            TAG,
            "extract[$hlsMasterKey] DONE pass=2 -> ${second.summary()} " +
                    "in ${System.currentTimeMillis() - startMs}ms"
        )
        return second
    }

    private suspend fun hedgedExtraction(
        hlsMasterKey: String,
        pass: Int
    ): ExtractionResult = coroutineScope {
        val primary: Deferred<ExtractionResult> = async {
            runOnce(hlsMasterKey, "p${pass}A")
        }
        val backup: Deferred<ExtractionResult> = async {
            delay(HEDGE_DELAY_MS.milliseconds)
            val primaryResult = if (primary.isCompleted) {
                runCatching { primary.await() }.getOrNull()
            } else {
                null
            }
            if (primaryResult is ExtractionResult.Success) {
                Log.d(
                    TAG,
                    "hedge[$hlsMasterKey] pass=$pass primary completed " +
                            "with Success before backup"
                )
                primaryResult
            } else {
                Log.d(
                    TAG,
                    "hedge[$hlsMasterKey] pass=$pass backup launched " +
                            "after ${HEDGE_DELAY_MS}ms"
                )
                runOnce(hlsMasterKey, "p${pass}B")
            }
        }

        try {
            val first = select {
                primary.onAwait { it }
                backup.onAwait { it }
            }
            Log.d(
                TAG,
                "hedge[$hlsMasterKey] pass=$pass first arrival -> ${first.summary()}"
            )
            if (first is ExtractionResult.Success) {
                cancelOther(primary, backup)
                return@coroutineScope first
            }

            val second = if (primary.isCompleted) backup.await() else primary.await()
            Log.d(
                TAG,
                "hedge[$hlsMasterKey] pass=$pass second arrival -> ${second.summary()}"
            )
            when {
                second is ExtractionResult.Success -> second
                first is ExtractionResult.Empty &&
                        second is ExtractionResult.Empty -> ExtractionResult.Empty
                else -> ExtractionResult.Incomplete
            }
        } catch (ce: CancellationException) {
            primary.cancel()
            backup.cancel()
            throw ce
        }
    }

    private suspend fun cancelOther(primary: Deferred<*>, backup: Deferred<*>) {
        runCatching {
            if (primary.isCompleted) backup.cancelAndJoin() else primary.cancelAndJoin()
        }
    }

    private suspend fun runOnce(hlsMasterKey: String, tag: String): ExtractionResult {
        val startMs = System.currentTimeMillis()
        return try {
            val extractor = limiter.withPermit {
                withTimeoutOrNull(EXTRACTION_TIMEOUT_MS.milliseconds) {
                    pageFetcher.fetch(hlsMasterKey)
                }
            } ?: run {
                Log.w(
                    TAG,
                    "runOnce[$hlsMasterKey][$tag] timeout " +
                            "in ${System.currentTimeMillis() - startMs}ms"
                )
                return ExtractionResult.Incomplete
            }
            val result = reader.read(hlsMasterKey, extractor)
            Log.d(
                TAG,
                "runOnce[$hlsMasterKey][$tag] -> ${result.summary()} " +
                        "in ${System.currentTimeMillis() - startMs}ms"
            )
            result
        } catch (ce: CancellationException) {
            Log.d(
                TAG,
                "runOnce[$hlsMasterKey][$tag] cancelled " +
                        "in ${System.currentTimeMillis() - startMs}ms"
            )
            throw ce
        } catch (e: Exception) {
            Log.w(
                TAG,
                "runOnce[$hlsMasterKey][$tag] error " +
                        "in ${System.currentTimeMillis() - startMs}ms: " +
                        "${e.javaClass.simpleName} ${e.message}"
            )
            ExtractionResult.Incomplete
        }
    }

    private fun nextEmptyBackoff(): EmptyBackoff {
        val now = System.currentTimeMillis()
        synchronized(recentEmptiesLock) {
            while (recentEmpties.isNotEmpty() &&
                now - recentEmpties.first() > EMPTY_TRACKING_WINDOW_MS
            ) {
                recentEmpties.removeFirst()
            }
            recentEmpties.addLast(now)

            val count = recentEmpties.size
            val factor = 1L shl (count - 1).coerceIn(0, 2)
            val delay = (EMPTY_BASE_BACKOFF_MS * factor).coerceAtMost(EMPTY_MAX_BACKOFF_MS)

            return EmptyBackoff(delayMs = delay, recentCount = count)
        }
    }

    private data class EmptyBackoff(
        val delayMs: Long,
        val recentCount: Int
    )

    private fun ExtractionResult.summary(): String = when (this) {
        is ExtractionResult.Success -> {
            val extracted = data
            "Success(title=${extracted.title}, durationMs=${extracted.durationMs}, " +
                    "hasUrl=${!extracted.streamingUrl.isNullOrBlank()}, " +
                    "expiresAt=${extracted.streamingUrlExpiresAt})"
        }
        ExtractionResult.Empty -> "Empty"
        ExtractionResult.Incomplete -> "Incomplete"
    }

    companion object {
        private const val TAG = "ResilientStreamExtractor"

        // Timeout total por intento individual contra NewPipe.
        private const val EXTRACTION_TIMEOUT_MS = 8_000L

        // Cuanto esperamos antes de lanzar el segundo intento en paralelo.
        // Un valor pequeno mejora TTFP cuando el primario se cuelga; uno grande
        // evita carga inutil en YouTube cuando el primario va bien.
        private const val HEDGE_DELAY_MS = 800L

        // Backoff entre la primera pasada Incomplete y la segunda pasada.
        private const val RETRY_BACKOFF_MS = 600L

        // Backoff base cuando la primera pasada fue Empty: confirma rapido si
        // YouTube esta tranquilo. Se duplica si hubo Empties recientes en la
        // ventana de tracking y se topea en EMPTY_MAX_BACKOFF_MS para no
        // bloquear demasiado al usuario.
        private const val EMPTY_BASE_BACKOFF_MS = 600L

        // Tope del backoff Empty cuando hay varios Empties seguidos en poco
        // tiempo: senal de presion en NewPipe/YouTube, mejor esperar.
        private const val EMPTY_MAX_BACKOFF_MS = 1_800L

        // Ventana rodante que usamos para contar Empties recientes y escalar
        // el backoff cuando hay presion sostenida.
        private const val EMPTY_TRACKING_WINDOW_MS = 60_000L
    }
}