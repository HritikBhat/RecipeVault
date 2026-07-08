package com.hritik.recipevault.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hritik.recipevault.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences @Inject constructor(
    private val context: Context
) {
    companion object {
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val USER_DATA = stringPreferencesKey("user_data")
    }

    val isUserLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_LOGGED_IN] ?: false
        }

    val userData: Flow<User?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_DATA]?.let {
                try {
                    Json.decodeFromString<User>(it)
                } catch (e: Exception) {
                    null
                }
            }
        }

    suspend fun saveUser(user: User) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = true
            preferences[USER_DATA] = Json.encodeToString(user)
        }
    }

    suspend fun clearUser() {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = false
            preferences.remove(USER_DATA)
        }
    }
}
