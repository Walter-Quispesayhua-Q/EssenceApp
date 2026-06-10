package com.essence.essenceapp.feature.playback.service

/**
 * Acciones y datos que la MediaSession necesita consultar fuera del servicio.
 *
 * Sirve como puente pequeño entre los controles externos del sistema
 * —notificación, Bluetooth, lock screen— y el controlador real de playback.
 */
interface MediaSessionCallbacks {
    fun onNext()

    fun onPrevious()

    fun onToggleLike()

    fun canSkipNext(): Boolean

    fun canSkipPrevious(): Boolean

    fun currentDurationMs(): Long
}