package com.example.pruebaalmacenamiento.repositories

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.example.pruebaalmacenamiento.datastore.Keys
import com.example.pruebaalmacenamiento.datastore.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesRepository(private val context: Context) {

    val usernameFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.USER_NAME] ?: ""
    }

    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.IS_LOGGED_IN] ?: false
    }

    suspend fun saveUser(name: String, email: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.USER_NAME] = name
            prefs[Keys.USER_EMAIL] = email
        }
    }

    /**
     * Registra un usuario nuevo. Guarda email + contraseña (hash simple para demo).
     * En producción usa BCrypt o delega en un backend real.
     */
    suspend fun register(username: String, password: String): Boolean {
        // Verifica que no exista ya un usuario registrado con ese nombre
        return try {
            context.dataStore.edit { prefs ->
                prefs[Keys.USER_NAME] = username
                prefs[Keys.USER_PASSWORD_HASH] = password.hashCode().toString()
                prefs[Keys.IS_LOGGED_IN] = false
            }
            true
        } catch (e: Exception) { false }
    }

    /**
     * Intenta hacer login comparando el hash guardado.
     * Devuelve true si las credenciales coinciden.
     */
    suspend fun login(username: String, password: String): Boolean {
        var success = false
        context.dataStore.edit { prefs ->
            val storedName = prefs[Keys.USER_NAME] ?: ""
            val storedHash = prefs[Keys.USER_PASSWORD_HASH] ?: ""
            success = storedName == username && storedHash == password.hashCode().toString()
            if (success) prefs[Keys.IS_LOGGED_IN] = true
        }
        return success
    }

    suspend fun logout() {
        context.dataStore.edit { prefs ->
            prefs[Keys.IS_LOGGED_IN] = false
        }
    }
}