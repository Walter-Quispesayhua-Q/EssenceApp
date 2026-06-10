package com.essence.essenceapp.core.extractor.youtube.protocol

import javax.inject.Inject
import javax.inject.Singleton

private const val DEFAULT_WARM_UP_VIDEO_ID = "dQw4w9WgXcQ"
private const val YOUTUBE_MUSIC_WATCH_URL = "https://music.youtube.com/watch?v="

/**
 * Define como la app prepara la extraccion de YouTube.
 *
 * Centraliza los clientes habilitados, fallbacks de stream y warm-up para que
 * los resolvers no repartan decisiones de protocolo por todo el flujo.
 */
@Singleton
class YoutubeClientPolicy @Inject constructor() {

    val iosClientEnabled: Boolean = true

    val progressiveFallbackEnabled: Boolean = true

    val hlsFallbackEnabled: Boolean = false

    val warmUpEnabled: Boolean = true

    val warmUpVideoId: String = DEFAULT_WARM_UP_VIDEO_ID

    val warmUpTimeoutMs: Long = 8_000L

    val warmUpUrl: String
        get() = "$YOUTUBE_MUSIC_WATCH_URL$warmUpVideoId"
}
