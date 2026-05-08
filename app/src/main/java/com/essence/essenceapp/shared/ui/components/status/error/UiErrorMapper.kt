package com.essence.essenceapp.shared.ui.components.status.error

import java.net.SocketTimeoutException
import java.net.UnknownHostException
import retrofit2.HttpException

fun Throwable.toUserMessage(): String = when (this) {
    is UnknownHostException -> "No hay conexión a internet."
    is SocketTimeoutException -> "La conexión está lenta. Intenta nuevamente."
    is HttpException -> when (code()) {
        400 -> "La búsqueda no es válida. Intenta con otra palabra."
        401 -> "Tu sesión ha expirado. Vuelve a iniciar sesión."
        403 -> "No tienes permiso para realizar esta acción."
        404 -> "No se encontraron resultados."
        in 500..599 -> "El servidor no está disponible. Intenta más tarde."
        else -> "No pudimos completar la solicitud. Intenta otra vez."
    }
    else -> "No pudimos completar la solicitud. Intenta otra vez."
}
