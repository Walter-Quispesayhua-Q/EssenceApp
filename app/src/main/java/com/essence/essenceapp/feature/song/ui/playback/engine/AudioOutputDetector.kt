package com.essence.essenceapp.feature.song.ui.playback.engine

import android.content.Context
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "AUDIO_OUTPUT"

/**
 * Detector reactivo del dispositivo de salida de audio activo.
 *
 * Expone un [StateFlow] con el [AudioOutputType] vigente y lo refresca
 * automáticamente cuando el usuario conecta o desconecta un periférico
 * (cable, Bluetooth, USB).
 *
 * ## Por qué existe
 *
 * La UI muestra un indicador visual del canal de salida (p. ej. icono
 * de auriculares BT en el MiniPlayer). Observar el [outputType] como
 * `StateFlow` permite que Compose recomponga sólo ese icono sin tocar
 * el resto del árbol.
 *
 * ## Prioridad de detección
 *
 * `WIRED_HEADSET`/`WIRED_HEADPHONES` > `USB_AUDIO` > `BLUETOOTH_*` > `PHONE_SPEAKER`.
 *
 * Los cables tienen prioridad sobre Bluetooth porque, si ambos están
 * conectados, Android encamina el audio al cable por defecto. La
 * distinción BT *headset* vs *speaker* se infiere de si el dispositivo
 * también expone una entrada (micrófono).
 *
 * ## Lifecycle
 *
 * Singleton con el mismo lifetime que el proceso. El callback queda
 * registrado tras la primera inyección; [release] puede llamarse para
 * desinstrumentar (útil en tests o apagado controlado). En producción
 * no es necesario invocarlo porque el callback se libera cuando el
 * proceso muere.
 */
@Singleton
class AudioOutputDetector @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val _outputType = MutableStateFlow(detectCurrent())
    val outputType: StateFlow<AudioOutputType> = _outputType.asStateFlow()

    private val deviceCallback = object : AudioDeviceCallback() {
        override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo>?) {
            refresh()
        }

        override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo>?) {
            refresh()
        }
    }

    init {
        audioManager.registerAudioDeviceCallback(deviceCallback, Handler(Looper.getMainLooper()))
        Log.d(TAG, "Initial output: ${_outputType.value.name} (${_outputType.value.label})")
    }

    /**
     * Libera los callbacks del [AudioManager]. Pensado para tests y
     * escenarios de apagado controlado; en producción no se invoca.
     */
    fun release() {
        audioManager.unregisterAudioDeviceCallback(deviceCallback)
    }

    private fun refresh() {
        val newType = detectCurrent()
        if (newType != _outputType.value) {
            Log.d(TAG, "Output changed: ${_outputType.value.name} → ${newType.name}")
            _outputType.value = newType
        }
    }

    /**
     * Inspecciona los dispositivos de salida conectados y devuelve
     * el de mayor prioridad según el orden
     * `cable > USB > Bluetooth > speaker`.
     *
     * La segunda pasada sobre el array de dispositivos separa BT
     * *headset* (tiene micrófono asociado) de BT *speaker* (no lo tiene)
     * para mostrar el icono correcto en la UI.
     */
    private fun detectCurrent(): AudioOutputType {
        val outputs = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)

        for (device in outputs) {
            when (device.type) {
                AudioDeviceInfo.TYPE_WIRED_HEADSET ->
                    return AudioOutputType.WIRED_HEADSET

                AudioDeviceInfo.TYPE_WIRED_HEADPHONES ->
                    return AudioOutputType.WIRED_HEADPHONES

                AudioDeviceInfo.TYPE_USB_HEADSET,
                AudioDeviceInfo.TYPE_USB_DEVICE ->
                    return AudioOutputType.USB_AUDIO
            }
        }

        for (device in outputs) {
            when (device.type) {
                AudioDeviceInfo.TYPE_BLUETOOTH_A2DP -> {
                    val hasInputCounterpart = audioManager
                        .getDevices(AudioManager.GET_DEVICES_INPUTS)
                        .any {
                            it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO ||
                                    it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP
                        }
                    return if (hasInputCounterpart) {
                        AudioOutputType.BLUETOOTH_HEADSET
                    } else {
                        AudioOutputType.BLUETOOTH_SPEAKER
                    }
                }

                AudioDeviceInfo.TYPE_BLUETOOTH_SCO ->
                    return AudioOutputType.BLUETOOTH_HEADSET
            }
        }

        return AudioOutputType.PHONE_SPEAKER
    }
}
