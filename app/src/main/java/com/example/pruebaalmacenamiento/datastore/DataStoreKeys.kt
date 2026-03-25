package com.example.pruebaalmacenamiento.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore(name = "settings")

object Keys {
    val USER_NAME           = stringPreferencesKey("user_name")
    val USER_EMAIL          = stringPreferencesKey("user_email")
    val USER_PASSWORD_HASH  = stringPreferencesKey("user_password_hash")
    val IS_LOGGED_IN        = booleanPreferencesKey("is_logged_in")
}