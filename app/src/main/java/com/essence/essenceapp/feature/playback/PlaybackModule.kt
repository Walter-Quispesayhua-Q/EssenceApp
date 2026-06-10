package com.essence.essenceapp.feature.playback

import com.essence.essenceapp.feature.playback.domain.PlaybackController
import com.essence.essenceapp.feature.playback.engine.AudioPlayerEngine
import com.essence.essenceapp.feature.playback.engine.mediacontroller.MediaControllerAudioPlayerEngine
import com.essence.essenceapp.feature.playback.manager.DefaultPlaybackController
import com.essence.essenceapp.feature.playback.manager.resolver.DefaultPlaybackSongResolver
import com.essence.essenceapp.feature.playback.manager.resolver.PlaybackSongResolver
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Conecta las interfaces publicas de playback con sus implementaciones reales.
 *
 * Mantiene las dependencias desacopladas: las pantallas y otros modulos piden
 * contratos simples, mientras Hilt decide que clase concreta debe usarse.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class PlaybackModule {

    @Binds
    @Singleton
    abstract fun bindPlaybackController(
        implementation: DefaultPlaybackController
    ): PlaybackController

    @Binds
    @Singleton
    abstract fun bindAudioPlayerEngine(
        implementation: MediaControllerAudioPlayerEngine
    ): AudioPlayerEngine

    @Binds
    @Singleton
    abstract fun bindPlaybackSongResolver(
        implementation: DefaultPlaybackSongResolver
    ): PlaybackSongResolver
}