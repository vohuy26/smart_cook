package com.example.smart_cook.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {

    private const val PREF_NAME =
        "theme_pref"

    private const val KEY_DARK_MODE =
        "dark_mode"

    fun saveTheme(
        context: Context,
        isDarkMode: Boolean
    ) {

        val sharedPreferences =
            context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
            )

        sharedPreferences.edit()
            .putBoolean(
                KEY_DARK_MODE,
                isDarkMode
            )
            .apply()
    }

    fun isDarkMode(
        context: Context
    ): Boolean {

        val sharedPreferences =
            context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
            )

        return sharedPreferences.getBoolean(
            KEY_DARK_MODE,
            false
        )
    }

    fun applyTheme(
        isDarkMode: Boolean
    ) {

        if (isDarkMode) {

            AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES
            )

        } else {

            AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }
}