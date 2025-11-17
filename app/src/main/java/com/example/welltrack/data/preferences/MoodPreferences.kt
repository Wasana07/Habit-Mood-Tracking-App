package com.example.welltrack.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.welltrack.data.models.Mood
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MoodPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREFS_NAME = "mood_prefs"
        private const val KEY_MOODS = "key_moods"
    }

    fun saveMoods(moods: List<Mood>) {
        val json = gson.toJson(moods)
        prefs.edit().putString(KEY_MOODS, json).apply()
    }

    fun loadMoods(): List<Mood> {
        val json = prefs.getString(KEY_MOODS, null)
        return if (json != null) {
            val type = object : TypeToken<List<Mood>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    fun addOrUpdateMood(mood: Mood) {
        val currentMoods = loadMoods().toMutableList()
        val existingIndex = currentMoods.indexOfFirst { it.id == mood.id && it.userId == mood.userId }

        if (existingIndex != -1) {
            currentMoods[existingIndex] = mood
        } else {
            currentMoods.add(mood)
        }
        saveMoods(currentMoods)
    }

    fun getMoodsForDate(date: String, userId: String): List<Mood> {
        return loadMoods().filter { it.userId == userId && it.date == date }
    }
}