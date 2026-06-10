package com.essence.essenceapp

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.essence.essenceapp.core.extractor.youtube.protocol.YoutubeProtocolInitializer
import com.essence.essenceapp.core.network.BackendWarmer
import com.essence.essenceapp.feature.playback.cache.MediaAudioCache
import com.essence.essenceapp.feature.playback.observers.PlaybackObserversStarter
import com.essence.essenceapp.ui.resilience.GlobalExceptionHandler
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@HiltAndroidApp
class EssenceApp : Application(), ImageLoaderFactory {

    /**
     * Puentes para pedir objetos de Hilt desde Application.
     *
     * Application es creada por Android muy temprano y no recibe dependencias
     * por constructor. Por eso usamos EntryPointAccessors para obtener solo los
     * objetos que deben arrancar a nivel global de la app.
     */
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface MediaCacheEntryPoint {
        fun mediaAudioCache(): MediaAudioCache
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ResilienceEntryPoint {
        fun globalExceptionHandler(): GlobalExceptionHandler
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface YoutubeProtocolEntryPoint {
        fun youtubeProtocolInitializer(): YoutubeProtocolInitializer
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface BackendWarmerEntryPoint {
        fun backendWarmer(): BackendWarmer
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface PlaybackObserversEntryPoint {
        fun playbackObserversStarter(): PlaybackObserversStarter
    }

    private val mediaAudioCache: MediaAudioCache by lazy {
        EntryPointAccessors.fromApplication(
            this,
            MediaCacheEntryPoint::class.java
        ).mediaAudioCache()
    }

    private val globalExceptionHandler: GlobalExceptionHandler by lazy {
        EntryPointAccessors.fromApplication(
            this,
            ResilienceEntryPoint::class.java
        ).globalExceptionHandler()
    }

    private val youtubeProtocolInitializer: YoutubeProtocolInitializer by lazy {
        EntryPointAccessors.fromApplication(
            this,
            YoutubeProtocolEntryPoint::class.java
        ).youtubeProtocolInitializer()
    }

    private val backendWarmer: BackendWarmer by lazy {
        EntryPointAccessors.fromApplication(
            this,
            BackendWarmerEntryPoint::class.java
        ).backendWarmer()
    }

    private val playbackObserversStarter: PlaybackObserversStarter by lazy {
        EntryPointAccessors.fromApplication(
            this,
            PlaybackObserversEntryPoint::class.java
        ).playbackObserversStarter()
    }

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Arranque global de servicios livianos de la app.
     *
     * Aqui se instalan protecciones, se prepara el protocolo de YouTube para
     * reducir el primer tiempo de extraccion, se calienta el backend y se
     * activan observers pasivos de playback como historial y metricas.
     */
    override fun onCreate() {
        super.onCreate()

        globalExceptionHandler.installAsDefault()

        playbackObserversStarter.start()

        youtubeProtocolInitializer.warmUp(appScope)

        backendWarmer.warmUp(appScope)

        mediaAudioCache.warmUp(appScope)
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)

        if (level >= TRIM_MEMORY_COMPLETE) {
            mediaAudioCache.release()
        }
    }

    override fun newImageLoader(): ImageLoader = ImageLoader.Builder(this)
        .memoryCache {
            MemoryCache.Builder(this)
                .maxSizePercent(IMAGE_MEMORY_CACHE_HEAP_PERCENT)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(cacheDir.resolve(IMAGE_DISK_CACHE_DIR))
                .maxSizeBytes(IMAGE_DISK_CACHE_BYTES)
                .build()
        }
        .crossfade(IMAGE_CROSSFADE_MS)
        .respectCacheHeaders(false)
        .build()

    private companion object {
        const val IMAGE_MEMORY_CACHE_HEAP_PERCENT = 0.25
        const val IMAGE_DISK_CACHE_BYTES = 100L * 1024 * 1024
        const val IMAGE_CROSSFADE_MS = 200
        const val IMAGE_DISK_CACHE_DIR = "image_cache"
    }
}