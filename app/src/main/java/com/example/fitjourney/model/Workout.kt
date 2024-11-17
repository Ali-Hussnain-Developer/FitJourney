package com.example.fitjourney.model

import com.example.fitjourney.enum.WorkoutType

data class Workout(
    val name: String,
    val description: String,
    val type: WorkoutType
)
