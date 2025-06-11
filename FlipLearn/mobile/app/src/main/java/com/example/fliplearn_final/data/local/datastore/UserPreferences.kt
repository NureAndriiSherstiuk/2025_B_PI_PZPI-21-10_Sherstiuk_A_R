package com.example.fliplearn_final.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val USER_EMAIL = stringPreferencesKey("user_email")
        val IS_LOCALLY_SIGNED_IN = booleanPreferencesKey("is_locally_signed_in")
        val SELECTED_THEME = stringPreferencesKey("selected_theme")
    }

    val userEmail: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[USER_EMAIL]
    }

    val isLocallySignedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[IS_LOCALLY_SIGNED_IN] ?: false
    }

    suspend fun saveUserEmail(email: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_EMAIL] = email
        }
    }

    suspend fun getUserEmail(): String? {
        return context.dataStore.data.map { prefs ->
            prefs[USER_EMAIL]
        }.firstOrNull()
    }


    suspend fun setLocallySignedIn(isSignedIn: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_LOCALLY_SIGNED_IN] = isSignedIn
        }
    }

    suspend fun clearUserData() {
        context.dataStore.edit { prefs ->
            prefs.remove(USER_EMAIL)
            prefs.remove(IS_LOCALLY_SIGNED_IN)
        }
    }

    val selectedTheme: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[SELECTED_THEME]
    }

    suspend fun saveSelectedTheme(theme: String) {
        context.dataStore.edit { prefs ->
            prefs[SELECTED_THEME] = theme
        }
    }

    suspend fun getSelectedTheme(): String? {
        return context.dataStore.data.map { prefs ->
            prefs[SELECTED_THEME]
        }.firstOrNull()
    }

}
