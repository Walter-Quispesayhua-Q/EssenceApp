package com.essence.essenceapp.feature.song.domain.resolver

/**
 * Errores que pueden aparecer mientras se prepara una cancion para sonar.
 *
 * Ayuda a distinguir si fallo el extractor, el backend, la URL del audio o
 * una validacion interna, sin depender solo de null o mensajes sueltos.
 */
sealed interface SongResolveError {

    val message: String

    data object MissingHlsMasterKey : SongResolveError {
        override val message: String = "Missing hlsMasterKey"
    }

    data class NetworkFailed(
        override val message: String
    ) : SongResolveError

    data class ExtractorFailed(
        override val message: String
    ) : SongResolveError

    data class BackendFailed(
        override val message: String
    ) : SongResolveError

    data class UrlInvalid(
        override val message: String
    ) : SongResolveError

    data class Unknown(
        override val message: String,
        val cause: Throwable? = null
    ) : SongResolveError
}
