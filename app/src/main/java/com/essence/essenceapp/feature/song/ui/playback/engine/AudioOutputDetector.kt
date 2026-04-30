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

//Detecta el dispositivo de salida de audio activo y emite
//cambios como [StateFlow]. Distingue entre speaker, audífonos
//con cable, Bluetooth con/sin micrófono, y USB.
@Singleton
class AudioOutputDetector @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val _outputType = MutableStateFlow(detectCurrent())
    val outputType: StateFlow<AudioOutputType> = _outputType.asStateFlow()

    init {
        audioManager.registerAudioDeviceCallback(object : AudioDeviceCallback() {
            override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo>?) {
                refresh()
            }

            override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo>?) {
                refresh()
            }
        }, Handler(Looper.getMainLooper()))

        Log.d(TAG, "Initial output: ${_outputType.value.name} (${_outputType.value.label})")
    }

    private fun refresh() {
        val newType = detectCurrent()
        if (newType != _outputType.value) {
            Log.d(TAG, "Output changed: ${_outputType.value.name} → ${newType.name}")
            _outputType.value = newType
        }
    }

//    Inspecciona los dispositivos de salida conectados y devuelve
//    el de mayor prioridad: cable > bluetooth > USB > speaker.
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
