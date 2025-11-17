package com.example.welltrack.data.models

data class Mood(
    val id: String,
    val userId: String,
    val moodType: String,
    val timestamp: Long,
    val date: String
)