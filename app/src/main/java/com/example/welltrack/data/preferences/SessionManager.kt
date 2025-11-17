package com.example.welltrack.data.preferences

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "session_prefs"
        private const val KEY_USER_ID = "key_user_id"
    }

    fun saveUserSession(userId: String) {
        prefs.edit().putString(KEY_USER_ID, userId).apply()
    }

    fun getLoggedInUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    fun clearSession() {
        prefs.edit().remove(KEY_USER_ID).apply()
    }
}