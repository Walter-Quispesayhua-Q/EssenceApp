package com.essence.essenceapp.core.storage

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.json.JSONObject

class TokenManager(private val context: Context) {
    private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

    private val cachedToken = AtomicReference<String?>(null)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        context.dataStore.data
            .map { it[TOKEN_KEY] }
            .onEach { cachedToken.set(it) }
            .launchIn(scope)
    }

    val token: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }

    fun getCachedToken(): String? = cachedToken.get()

    suspend fun saveToken(token: String) {
        cachedToken.set(token)
        context.dataStore.edit { it[TOKEN_KEY] = token }
    }

    suspend fun saveTokenAndUserId(token: String) {
        val userId = decodeUserId(token)
        cachedToken.set(token)
        context.dataStore.edit {
            it[TOKEN_KEY] = token
            if (userId != null) {
                it[USER_ID_KEY] = userId
            }
        }
    }

    private fun decodeUserId(token: String): Long? {
        return try {
            val payloadPart = token.split(".").getOrNull(1) ?: return null
            val paddedPayload = payloadPart.padEnd(
                ((payloadPart.length + 3) / 4) * 4,
                '='
            )

            val decoded = String(
                Base64.decode(
                    paddedPayload,
                    Base64.URL_SAFE or Base64.NO_WRAP
                )
            )

            val json = JSONObject(decoded)

            when {
                json.has("id") -> json.optLong("id").takeIf { it != 0L || json.opt("id")?.toString() == "0" }
                json.has("userId") -> json.optLong("userId").takeIf { it != 0L || json.opt("userId")?.toString() == "0" }
                json.has("user_id") -> json.optLong("user_id").takeIf { it != 0L || json.opt("user_id")?.toString() == "0" }
                json.has("sub") -> json.optString("sub").toLongOrNull()
                else -> null
            }
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getUserId(): Long? {
        return context.dataStore.data.map { it[USER_ID_KEY] }.first()
    }

    suspend fun clear() {
        cachedToken.set(null)
        context.dataStore.edit { it.clear() }
    }

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_ID_KEY = longPreferencesKey("user_id")
    }
}