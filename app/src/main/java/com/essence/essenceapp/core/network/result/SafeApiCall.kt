package com.essence.essenceapp.core.network.result

import com.google.gson.JsonParseException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

suspend inline fun <T> safeApiCall(crossinline block: suspend () -> T): NetworkResult<T> {
    return try {
        NetworkResult.Success(block())
    } catch (ce: CancellationException) {
        throw ce
    } catch (e: Throwable) {
        NetworkResult.Error(e.toNetworkException())
    }
}

fun Throwable.toNetworkException(): NetworkException = when (this) {
    is NetworkException -> this
    is SocketTimeoutException -> NetworkException.Timeout(cause = this)
    is UnknownHostException -> NetworkException.NoConnection(cause = this)
    is SSLException -> NetworkException.NoConnection(
        message = "Error de conexion segura",
        cause = this
    )
    is HttpException -> when (val httpCode = code()) {
        in 400..400 -> NetworkException.BadRequest(code = httpCode, cause = this)
        401 -> NetworkException.Unauthorized(cause = this)
        403 -> NetworkException.Forbidden(cause = this)
        404 -> NetworkException.NotFound(cause = this)
        in 405..499 -> NetworkException.BadRequest(
            message = "Solicitud invalida ($httpCode)",
            code = httpCode,
            cause = this
        )
        in 500..599 -> NetworkException.ServerError(
            message = "Error del servidor ($httpCode)",
            code = httpCode,
            cause = this
        )
        else -> NetworkException.Unknown(
            message = "HTTP $httpCode",
            cause = this
        )
    }
    is IOException -> NetworkException.NoConnection(cause = this)
    is JsonParseException -> NetworkException.Serialization(cause = this)
    else -> NetworkException.Unknown(message = message, cause = this)
}
