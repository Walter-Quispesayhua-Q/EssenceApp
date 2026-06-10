package com.essence.essenceapp.feature.playback.cache

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "MEDIA_CACHE"

/**
 * Cache local para los bytes de audio reproducidos por Media3.
 *
 * Mantiene una sola instancia de SimpleCache para que ExoPlayer y el prefetch
 * compartan los mismos fragmentos descargados sin duplicar archivos en disco.
 */
@Singleton
@OptIn(UnstableApi::class)
class MediaAudioCache @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val cache: SimpleCache by lazy {
        val cacheDir = File(context.cacheDir, CACHE_DIR_NAME).apply {
            if (!exists()) mkdirs()
        }

        Log.d(TAG, "Initializing media cache at ${cacheDir.absolutePath}")

        SimpleCache(
            cacheDir,
            LeastRecentlyUsedCacheEvictor(MAX_BYTES),
            StandaloneDatabaseProvider(context)
        )
    }

    /**
     * Inicializa el SimpleCache en background para evitar que el primer play
     * pague el costo del init (creacion del directorio + apertura de la
     * StandaloneDatabaseProvider). Es seguro llamarlo varias veces: el lazy
     * solo construye una vez.
     */
    fun warmUp(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            try {
                cache
            } catch (error: Exception) {
                Log.w(TAG, "Cache warm-up failed: ${error.message}")
            }
        }
    }

    fun release() {
        try {
            cache.release()
            Log.d(TAG, "Media cache released")
        } catch (error: Exception) {
            Log.e(TAG, "Error releasing media cache: ${error.message}", error)
        }
    }

    companion object {
        private const val CACHE_DIR_NAME = "media_cache"
        private const val MAX_BYTES = 200L * 1024L * 1024L
    }
}