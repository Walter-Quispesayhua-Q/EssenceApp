package com.essence.essenceapp.feature.song.data.repository.resolver.remote

/**
 * Resultado de una llamada a la api dentro del paquete remote.
 *
 * Permite distinguir entre los tres desenlaces tipicos: la api respondio
 * correctamente, hubo un fallo de red antes de poder hablar con la api,
 * o la api respondio pero con un error que el cliente no puede arreglar.
 */
sealed interface RemoteResult<out T> {

    data class Success<T>(val value: T) : RemoteResult<T>

    data object NetworkError : RemoteResult<Nothing>

    data class ApiError(val message: String) : RemoteResult<Nothing>
}