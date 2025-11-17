package com.example.welltrack.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.welltrack.data.models.Habit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HabitPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREFS_NAME = "habit_prefs"
        private const val KEY_HABITS = "key_habits"
    }

    fun saveHabits(habits: List<Habit>) {
        val json = gson.toJson(habits)
        prefs.edit().putString(KEY_HABITS, json).apply()
    }

    fun loadHabits(): List<Habit> {
        val json = prefs.getString(KEY_HABITS, null)
        return if (json != null) {
            val type = object : TypeToken<List<Habit>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    /**
     * Adds a new habit or updates an existing one.
     * NEW: Includes logic to automatically mark a habit as completed if progress meets the goal.
     */
    fun addOrUpdateHabit(habit: Habit) {
        val currentHabits = loadHabits().toMutableList()
        val existingIndex = currentHabits.indexOfFirst { it.id == habit.id && it.userId == habit.userId && it.date == habit.date }

        val isHabitCompleted = habit.currentProgress >= habit.goalNumber
        val updatedHabit = habit.copy(isCompleted = isHabitCompleted)

        if (existingIndex != -1) {
            currentHabits[existingIndex] = updatedHabit
        } else {
            currentHabits.add(updatedHabit)
        }
        saveHabits(currentHabits)
    }

    fun deleteHabit(habitId: String, userId: String) {
        val currentHabits = loadHabits().toMutableList()
        currentHabits.removeAll { it.id == habitId && it.userId == userId }
        saveHabits(currentHabits)
    }

    fun getHabitById(habitId: String, userId: String): Habit? {
        return loadHabits().find { it.id == habitId && it.userId == userId }
    }
}