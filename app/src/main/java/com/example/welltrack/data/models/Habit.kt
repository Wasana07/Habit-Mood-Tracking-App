package com.example.welltrack.data.models

data class Habit(
    val id: String,
    val userId: String,
    val name: String,
    val goalType: String,
    val goalNumber: Int,
    val currentProgress: Int = 0,
    val reminderTime: String,
    val date: String,
    val creationDate: String,
    val isCompleted: Boolean = false
)