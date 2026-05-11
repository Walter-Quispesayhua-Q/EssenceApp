package com.essence.essenceapp

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.essence.essenceapp.core.extractor.NewPipeInitializer
import com.essence.essenceapp.core.network.BackendWarmer
import com.essence.essenceapp.core.network.qualifier.NewPipeOkHttpClient
import com.essence.essenceapp.feature.song.ui.playback.engine.MediaAudioCache
import com.essence.essenceapp.ui.resilience.GlobalExceptionHandler
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient

@HiltAndroidApp
class EssenceApp : Application(), ImageLoaderFactory {

    /**
     * EntryPoint para acceder a beans de Hilt desde la clase Application,
     * que no soporta inyeccion directa de fields con @Inject.
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
    interface NewPipeNetworkEntryPoint {
        @NewPipeOkHttpClient
        fun newPipeOkHttpClient(): OkHttpClient
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface BackendWarmerEntryPoint {
        fun backendWarmer(): BackendWarmer
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

    private val newPipeOkHttpClient: OkHttpClient by lazy {
        EntryPointAccessors.fromApplication(
            this,
            NewPipeNetworkEntryPoint::class.java
        ).newPipeOkHttpClient()
    }

    private val backendWarmer: BackendWarmer by lazy {
        EntryPointAccessors.fromApplication(
            this,
            BackendWarmerEntryPoint::class.java
        ).backendWarmer()
    }

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        globalExceptionHandler.installAsDefault()
        NewPipeInitializer.setOkHttpClient(newPipeOkHttpClient)
        NewPipeInitializer.warmUp(appScope)
        backendWarmer.warmUp(appScope)
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