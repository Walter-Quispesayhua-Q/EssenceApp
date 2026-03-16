package com.essence.essenceapp.core.network.storage

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONObject

class TokenManager(private val context: Context) {
    private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_ID_KEY = longPreferencesKey("user_id")
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { it[TOKEN_KEY] = token }
    }

    val token: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }

    suspend fun saveTokenAndUserId(token: String) {
        val userId = decodeUserId(token)

        Log.e("AUTH_DEBUG", "decodedUserId=$userId")

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

            Log.e("AUTH_DEBUG", "jwtPayload=$decoded")

            val json = JSONObject(decoded)

            when {
                json.has("id") -> json.optLong("id").takeIf { it != 0L || json.opt("id")?.toString() == "0" }
                json.has("userId") -> json.optLong("userId").takeIf { it != 0L || json.opt("userId")?.toString() == "0" }
                json.has("user_id") -> json.optLong("user_id").takeIf { it != 0L || json.opt("user_id")?.toString() == "0" }
                json.has("sub") -> json.optString("sub").toLongOrNull()
                else -> null
            }
        } catch (e: Exception) {
            Log.e("AUTH_DEBUG", "decodeUserId error=${e.message}", e)
            null
        }
    }

    suspend fun getUserId(): Long? {
        return context.dataStore.data.map { it[USER_ID_KEY] }.first()
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}