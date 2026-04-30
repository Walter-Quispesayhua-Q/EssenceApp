package com.essence.essenceapp.feature.profile.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.essence.essenceapp.feature.profile.domain.model.UserProfile
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.profileDataStore by preferencesDataStore(name = "profile_prefs")

@Singleton
class ProfileLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {
    private val profileKey = stringPreferencesKey(KEY_PROFILE_JSON)

    val cachedProfile: Flow<UserProfile?> = context.profileDataStore.data
        .map { prefs ->
            prefs[profileKey]?.let { json ->
                runCatching { gson.fromJson(json, UserProfile::class.java) }.getOrNull()
            }
        }
        .distinctUntilChanged()

    suspend fun save(profile: UserProfile) {
        val json = gson.toJson(profile)
        context.profileDataStore.edit { prefs ->
            prefs[profileKey] = json
        }
    }

    suspend fun clear() {
        context.profileDataStore.edit { prefs ->
            prefs.clear()
        }
    }

    private companion object {
        const val KEY_PROFILE_JSON = "user_profile_json"
    }
}
