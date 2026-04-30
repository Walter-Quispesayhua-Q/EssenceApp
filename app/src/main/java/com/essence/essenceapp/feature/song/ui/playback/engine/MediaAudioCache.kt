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

@Singleton
@OptIn(UnstableApi::class)
class MediaAudioCache @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val cache: SimpleCache by lazy {
        val cacheDir = File(context.cacheDir, CACHE_DIR_NAME).apply {
            if (!exists()) mkdirs()
        }
        Log.d(TAG, "Inicializando SimpleCache en ${cacheDir.absolutePath} (max=${MAX_BYTES / 1024 / 1024} MB)")

        val evictor = LeastRecentlyUsedCacheEvictor(MAX_BYTES)
        val databaseProvider = StandaloneDatabaseProvider(context)

        SimpleCache(cacheDir, evictor, databaseProvider)
    }

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

        // Tamano maximo del cache: 100 MB.
        private const val MAX_BYTES = 100L * 1024L * 1024L
    }
}
