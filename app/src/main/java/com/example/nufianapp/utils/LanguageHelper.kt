package com.example.nufianapp.utils

import android.content.Context
import android.content.SharedPreferences

private const val PREF_NAME = "nufian_prefs"
private const val LANGUAGE_KEY = "language_key"

object LanguageHelper {

    // Save the selected language to SharedPreferences
    fun saveUserPreferredLanguage(context: Context, language: String) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(LANGUAGE_KEY, language).apply()
    }

    // Retrieve the saved language from SharedPreferences
    fun getUserPreferredLanguage(context: Context): String? {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(LANGUAGE_KEY, "en") // Default to "en" if not set
    }
}
