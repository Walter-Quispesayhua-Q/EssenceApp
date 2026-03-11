package com.essence.essenceapp.core.network.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import android.util.Base64
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
        context.dataStore.edit {
            it[TOKEN_KEY] = token
            if (userId != null) it[USER_ID_KEY] = userId
        }
    }
    private fun decodeUserId(token: String): Long? {
        return try {
            val payload = token.split(".")[1]
            val decoded = String(Base64.decode(payload, Base64.URL_SAFE))
            val json = JSONObject(decoded)
            json.getLong("id")
        } catch (e: Exception) {
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
