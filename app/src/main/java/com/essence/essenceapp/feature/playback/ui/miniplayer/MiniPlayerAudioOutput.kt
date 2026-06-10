package com.essence.essenceapp.feature.playback.ui.miniplayer

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.Usb
import androidx.compose.ui.graphics.vector.ImageVector
import com.essence.essenceapp.feature.playback.engine.AudioOutputType

/**
 * Traduce la salida de audio tecnica a texto e icono de UI.
 *
 * El engine solo dice el tipo de salida. La UI decide como representarla.
 */
internal data class MiniPlayerAudioOutputInfo(
    val label: String,
    val icon: ImageVector
)

internal fun AudioOutputType.toMiniPlayerAudioOutputInfo(): MiniPlayerAudioOutputInfo =
    when (this) {
        AudioOutputType.PHONE_SPEAKER -> MiniPlayerAudioOutputInfo(
            label = "Altavoz",
            icon = Icons.Default.PhoneAndroid
        )

        AudioOutputType.WIRED_HEADPHONES -> MiniPlayerAudioOutputInfo(
            label = "Audifonos",
            icon = Icons.Default.Headphones
        )

        AudioOutputType.WIRED_HEADSET -> MiniPlayerAudioOutputInfo(
            label = "Audifonos",
            icon = Icons.Default.HeadsetMic
        )

        AudioOutputType.BLUETOOTH_HEADSET -> MiniPlayerAudioOutputInfo(
            label = "Bluetooth",
            icon = Icons.Default.HeadsetMic
        )

        AudioOutputType.BLUETOOTH_SPEAKER -> MiniPlayerAudioOutputInfo(
            label = "Bluetooth",
            icon = Icons.Default.Speaker
        )

        AudioOutputType.USB_AUDIO -> MiniPlayerAudioOutputInfo(
            label = "USB",
            icon = Icons.Default.Usb
        )

        AudioOutputType.UNKNOWN -> MiniPlayerAudioOutputInfo(
            label = "Audio",
            icon = Icons.Default.Speaker
        )
    }