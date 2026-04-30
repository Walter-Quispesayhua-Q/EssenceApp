package com.essence.essenceapp.core.network.result

sealed class NetworkException(
    override val message: String?,
    override val cause: Throwable? = null
) : Exception(message, cause) {

    data class NoConnection(
        override val message: String? = "Sin conexion a internet",
        override val cause: Throwable? = null
    ) : NetworkException(message, cause)

    data class Timeout(
        override val message: String? = "La peticion tardo demasiado",
        override val cause: Throwable? = null
    ) : NetworkException(message, cause)

    data class Unauthorized(
        override val message: String? = "Sesion expirada",
        override val cause: Throwable? = null
    ) : NetworkException(message, cause)

    data class Forbidden(
        override val message: String? = "Acceso denegado",
        override val cause: Throwable? = null
    ) : NetworkException(message, cause)

    data class NotFound(
        override val message: String? = "Recurso no encontrado",
        override val cause: Throwable? = null
    ) : NetworkException(message, cause)

    data class BadRequest(
        override val message: String? = "Solicitud invalida",
        val code: Int = 400,
        override val cause: Throwable? = null
    ) : NetworkException(message, cause)

    data class ServerError(
        override val message: String? = "Error del servidor",
        val code: Int = 500,
        override val cause: Throwable? = null
    ) : NetworkException(message, cause)

    data class Serialization(
        override val message: String? = "Error procesando la respuesta",
        override val cause: Throwable? = null
    ) : NetworkException(message, cause)

    data class Unknown(
        override val message: String? = "Error desconocido",
        override val cause: Throwable? = null
    ) : NetworkException(message, cause)
}
