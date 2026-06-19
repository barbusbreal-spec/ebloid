package com.ebloid.store.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "ebloid_session")

/**
 * Хранит токен авторизации и имя пользователя в DataStore.
 * Токен читается синхронно при создании OkHttp-клиента через [cachedToken].
 */
class SessionManager(private val context: Context) {

    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[KEY_TOKEN] }
    val usernameFlow: Flow<String?> = context.dataStore.data.map { it[KEY_USERNAME] }

    @Volatile
    var cachedToken: String? = null
        private set

    suspend fun save(token: String, username: String) {
        cachedToken = token
        context.dataStore.edit {
            it[KEY_TOKEN] = token
            it[KEY_USERNAME] = username
        }
    }

    suspend fun clear() {
        cachedToken = null
        context.dataStore.edit { it.clear() }
    }

    companion object {
        private val KEY_TOKEN = stringPreferencesKey("token")
        private val KEY_USERNAME = stringPreferencesKey("username")
    }
}
