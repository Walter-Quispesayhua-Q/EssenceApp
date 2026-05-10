package com.essence.essenceapp.feature.song.ui.playback.engine

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.Usb
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Tipos de canal de salida de audio que la UI puede representar.
 *
 * Cada valor lleva anexo:
 * - [label]: etiqueta humana, en español, para mostrar en bottom-sheets
 *   o tooltips (p. ej. "Bluetooth" en el diálogo de selector de salida).
 * - [icon]: icono de Material que acompaña la etiqueta en el MiniPlayer
 *   y en el diálogo de cambio de salida.
 *
 * El mapeo viene de [AudioOutputDetector.detectCurrent], que traduce los
 * códigos crudos de [android.media.AudioDeviceInfo] a este enum más
 * conveniente para UI.
 */
enum class AudioOutputType(
    val label: String,
    val icon: ImageVector
) {
    /** Altavoz integrado del teléfono. Salida por defecto sin periféricos. */
    PHONE_SPEAKER("Altavoz", Icons.Default.PhoneAndroid),

    /** Audífonos cableados sin micrófono (conector 3.5 mm). */
    WIRED_HEADPHONES("Audífonos", Icons.Default.Headphones),

    /** Audífonos cableados con micrófono (conector 3.5 mm o USB-C). */
    WIRED_HEADSET("Audífonos", Icons.Default.HeadsetMic),

    /** Auriculares Bluetooth con micrófono (earbuds, BT headsets). */
    BLUETOOTH_HEADSET("Bluetooth", Icons.Default.HeadsetMic),

    /** Altavoz Bluetooth sin micrófono (p. ej. parlantes portátiles). */
    BLUETOOTH_SPEAKER("Bluetooth", Icons.Default.Speaker),

    /** Dispositivo USB (DAC, interfaz de audio, headset USB). */
    USB_AUDIO("USB", Icons.Default.Usb)
}
