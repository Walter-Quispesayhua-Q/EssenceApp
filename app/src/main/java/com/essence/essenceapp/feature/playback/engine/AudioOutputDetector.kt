package com.essence.essenceapp.feature.playback.engine

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Detecta por dónde está saliendo el audio actualmente.
 *
 * Traduce los dispositivos de audio de Android a un tipo simple que playback
 * puede exponer a la UI sin depender de APIs crudas del sistema.
 */
class AudioOutputDetector @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun detectCurrent(): AudioOutputType {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        val outputs = devices.mapNotNull { it.toOutputTypeOrNull() }

        return outputs.firstOrNull { it != AudioOutputType.PHONE_SPEAKER }
            ?: outputs.firstOrNull()
            ?: AudioOutputType.UNKNOWN
    }

    private fun AudioDeviceInfo.toOutputTypeOrNull(): AudioOutputType? =
        when (type) {
            AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> AudioOutputType.PHONE_SPEAKER
            AudioDeviceInfo.TYPE_WIRED_HEADPHONES -> AudioOutputType.WIRED_HEADPHONES
            AudioDeviceInfo.TYPE_WIRED_HEADSET -> AudioOutputType.WIRED_HEADSET
            AudioDeviceInfo.TYPE_BLUETOOTH_A2DP -> AudioOutputType.BLUETOOTH_SPEAKER
            AudioDeviceInfo.TYPE_BLUETOOTH_SCO -> AudioOutputType.BLUETOOTH_HEADSET
            AudioDeviceInfo.TYPE_USB_DEVICE,
            AudioDeviceInfo.TYPE_USB_HEADSET -> AudioOutputType.USB_AUDIO
            else -> null
        }
}