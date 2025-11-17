package com.example.welltrack.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.welltrack.data.models.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UserPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREFS_NAME = "user_prefs"
        private const val KEY_USERS = "key_users"
    }

    fun saveUser(user: User) {
        val currentUsers = getAllUsers().toMutableList()
        val existingIndex = currentUsers.indexOfFirst { it.id == user.id }
        if (existingIndex != -1) {
            currentUsers[existingIndex] = user
        } else {
            currentUsers.add(user)
        }
        val json = gson.toJson(currentUsers)
        prefs.edit().putString(KEY_USERS, json).apply()
    }

    fun getAllUsers(): List<User> {
        val json = prefs.getString(KEY_USERS, null)
        return if (json != null) {
            val type = object : TypeToken<List<User>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    fun login(email: String, password: String): User? {
        val user = getAllUsers().find { it.email == email }
        return if (user != null && user.password == password) user else null
    }

    fun getUserById(userId: String): User? {
        return getAllUsers().find { it.id == userId }
    }
}