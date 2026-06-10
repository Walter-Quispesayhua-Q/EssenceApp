package com.essence.essenceapp.feature.playback.engine

/**
 * Tipo de salida de audio detectada en el dispositivo.
 *
 * Mantiene solo información de dominio técnico. Los iconos o textos visuales
 * se definen en la UI para que el engine no dependa de Compose.
 */
enum class AudioOutputType {
    PHONE_SPEAKER,
    WIRED_HEADPHONES,
    WIRED_HEADSET,
    BLUETOOTH_HEADSET,
    BLUETOOTH_SPEAKER,
    USB_AUDIO,
    UNKNOWN
}