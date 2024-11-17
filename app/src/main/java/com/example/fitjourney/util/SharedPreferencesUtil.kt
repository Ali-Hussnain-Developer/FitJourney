package com.example.fitjourney.util


import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesUtil {
    private const val PREF_NAME = "FitJourneyPrefs"
    private const val STEP_GOAL_KEY = "step_goal_key"

    // Get SharedPreferences instance
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // Save step goal
    fun saveStepGoal(context: Context, goal: Int) {
        val sharedPreferences = getSharedPreferences(context)
        sharedPreferences.edit().putInt(STEP_GOAL_KEY, goal).apply()
    }

    // Get saved step goal (default is 10000)
    fun getStepGoal(context: Context): Int {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getInt(STEP_GOAL_KEY, 10000) // Default is 10000 steps
    }
}
