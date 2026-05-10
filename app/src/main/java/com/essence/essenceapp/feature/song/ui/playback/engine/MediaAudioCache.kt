package com.essence.essenceapp.feature.song.ui.playback.engine

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "MEDIA_CACHE"

/**
 * Capa de almacenamiento persistente para los streams de audio reproducidos
 * por el [androidx.media3.exoplayer.ExoPlayer].
 *
 * Envuelve una única instancia de [SimpleCache] que viven tanto el
 * reproductor ([CacheDataSourceProvider]) como el prefetch
 * ([MediaPrefetcher]). Así los bytes que se pre-descargan quedan
 * disponibles inmediatamente cuando el usuario pulsa Play.
 *
 * ## Política de almacenamiento
 *
 * - **Directorio**: [Context.cacheDir]/`media_cache`. Al estar bajo
 *   `cacheDir`, el sistema puede purgarlo automáticamente si el
 *   dispositivo se queda sin espacio; no hay pérdida funcional, sólo
 *   se re-descargará bajo demanda.
 * - **Tope**: 200 MB controlado por [LeastRecentlyUsedCacheEvictor].
 *   Cuando el cache supera este umbral, Media3 elimina los bloques
 *   menos recientemente usados hasta bajar del límite. 200 MB
 *   equivalen aproximadamente a 20–30 canciones completas en opus
 *   de alta calidad.
 * - **Metadata**: un [StandaloneDatabaseProvider] guarda índice de
 *   fragmentos en una base SQLite dentro del mismo directorio. Evita
 *   usar la DB principal de la app (no contamina su esquema ni sus
 *   migraciones).
 *
 * ## Contrato single-writer
 *
 * [SimpleCache] exige que exista **una sola instancia** por
 * directorio de cache en todo el proceso. Por eso este componente es
 * `@Singleton` y la propiedad [cache] es `by lazy`: garantiza una
 * única inicialización, thread-safe, perezosa hasta el primer uso
 * real.
 */
@Singleton
@OptIn(UnstableApi::class)
class MediaAudioCache @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Cache compartido entre reproductor y prefetch. Se inicializa la
     * primera vez que un consumidor lo pide y vive hasta que el proceso
     * muere o se invoca [release].
     */
    val cache: SimpleCache by lazy {
        val cacheDir = File(context.cacheDir, CACHE_DIR_NAME).apply {
            if (!exists()) mkdirs()
        }
        Log.d(TAG, "Inicializando SimpleCache en ${cacheDir.absolutePath} (max=${MAX_BYTES / 1024 / 1024} MB)")

        val evictor = LeastRecentlyUsedCacheEvictor(MAX_BYTES)
        val databaseProvider = StandaloneDatabaseProvider(context)

        SimpleCache(cacheDir, evictor, databaseProvider)
    }

    /**
     * Libera los file handles y cierra la DB de metadata. Tras este
     * punto, el cache queda inutilizable: no se debe volver a acceder
     * a [cache]. Pensado para tests controlados y shutdown ordenado.
     */
    fun release() {
        try {
            cache.release()
            Log.d(TAG, "SimpleCache liberado")
        } catch (e: Exception) {
            Log.e(TAG, "Error liberando cache: ${e.message}", e)
        }
    }

    companion object {
        private const val CACHE_DIR_NAME = "media_cache"

        /**
         * Tamaño máximo del cache en disco: 200 MB.
         *
         * Cubre 20–30 canciones completas en codecs modernos (Opus/AAC)
         * y deja margen para que el sistema operativo no sienta presión
         * en dispositivos con poco almacenamiento.
         */
        private const val MAX_BYTES = 200L * 1024L * 1024L
    }
}
