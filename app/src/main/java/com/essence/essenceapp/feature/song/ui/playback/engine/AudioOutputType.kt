package com.essence.essenceapp.feature.song.ui.playback.engine

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.Usb
import androidx.compose.ui.graphics.vector.ImageVector

enum class AudioOutputType(
    val label: String,
    val icon: ImageVector
) {
    PHONE_SPEAKER("Altavoz", Icons.Default.PhoneAndroid),
    WIRED_HEADPHONES("Audífonos", Icons.Default.Headphones),
    WIRED_HEADSET("Audífonos", Icons.Default.HeadsetMic),
    BLUETOOTH_HEADSET("Bluetooth", Icons.Default.HeadsetMic),
    BLUETOOTH_SPEAKER("Bluetooth", Icons.Default.Speaker),
    USB_AUDIO("USB", Icons.Default.Usb)
}
