package com.essence.essenceapp.feature.auth.register.data.repository

import com.essence.essenceapp.feature.auth.register.data.api.RegisterApiService
import com.essence.essenceapp.feature.auth.register.data.mapper.registerRequestToDto
import com.essence.essenceapp.feature.auth.register.data.mapper.registerToDomain
import com.essence.essenceapp.feature.auth.register.domain.model.Register
import com.essence.essenceapp.feature.auth.register.domain.model.RegisterRequest
import com.essence.essenceapp.feature.auth.register.domain.repository.RegisterEmailAlreadyUsedException
import com.essence.essenceapp.feature.auth.register.domain.repository.RegisterRepository
import com.essence.essenceapp.feature.auth.register.domain.repository.RegisterSubmissionException
import com.google.gson.Gson
import java.text.Normalizer
import retrofit2.HttpException

class RegisterRepositoryImpl(
    private val apiService: RegisterApiService,
    private val gson: Gson
) : RegisterRepository {

    override suspend fun getAvailableUsername(query: String): Boolean {
        val response = apiService.getAvailableUsername(query)
        return response?.data
            ?: throw IllegalStateException("Respuesta invalida al verificar username")
    }

    override suspend fun createUser(registerRequest: RegisterRequest): Register? {
        return try {
            val request = registerRequest.registerRequestToDto()
            val response = apiService.createUser(request)

            response?.data?.registerToDomain()
                ?: response?.message
                    ?.takeIf { it.isNotBlank() }
                    ?.let { throw RegisterSubmissionException(it) }
        } catch (error: HttpException) {
            throw error.toRegisterException()
        }
    }

    private fun HttpException.toRegisterException(): Exception {
        val apiMessage = extractApiMessage()
        return when {
            isEmailAlreadyUsed(code(), apiMessage) -> RegisterEmailAlreadyUsedException()
            !apiMessage.isNullOrBlank() -> RegisterSubmissionException(apiMessage)
            else -> RegisterSubmissionException("No se pudo completar el registro.")
        }
    }

    private fun HttpException.extractApiMessage(): String? {
        val errorBody = response()?.errorBody() ?: return null
        return try {
            errorBody.charStream().use { reader ->
                gson.fromJson(reader, RegisterErrorResponseDto::class.java)
                    ?.message
                    ?.takeIf { it.isNotBlank() }
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun isEmailAlreadyUsed(statusCode: Int, apiMessage: String?): Boolean {
        if (statusCode == 409) {
            return true
        }

        val normalizedMessage = apiMessage
            ?.normalizeForComparison()
            ?: return false

        val mentionsEmail = normalizedMessage.contains("correo") || normalizedMessage.contains("email")
        val mentionsDuplicate = listOf(
            "registrado",
            "registrada",
            "existe",
            "duplicado",
            "duplicate",
            "already",
            "taken",
            "usado",
            "uso"
        ).any(normalizedMessage::contains)

        return mentionsEmail && mentionsDuplicate
    }

    private fun String.normalizeForComparison(): String {
        val normalized = Normalizer.normalize(this, Normalizer.Form.NFD)
        return DIACRITICS_REGEX.replace(normalized, "").lowercase()
    }

    private data class RegisterErrorResponseDto(
        val message: String? = null
    )

    private companion object {
        val DIACRITICS_REGEX = "\\p{M}+".toRegex()
    }
}
