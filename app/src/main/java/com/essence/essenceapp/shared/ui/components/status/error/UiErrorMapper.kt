package com.essence.essenceapp.shared.ui.components.status.error

import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Throwable.toUserMessage(): String = when (this) {
    is UnknownHostException -> "No hay conexión a internet."
    is SocketTimeoutException -> "La conexión está lenta. Intenta nuevamente."
    else -> "No pudimos completar la solicitud. Intenta otra vez."
}
