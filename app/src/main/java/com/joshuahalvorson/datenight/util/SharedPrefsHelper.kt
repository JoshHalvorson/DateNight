package com.joshuahalvorson.datenight.util

import android.content.SharedPreferences

class SharedPrefsHelper(private val sharedPrefs: SharedPreferences?) {
    companion object {
        const val PREFERENCE_FILE_KEY = "saved_restaurants"
        const val RESTAURANT_ID_KEY = "restaurant_id"
        const val RESTAURANT_NAME_KEY = "restaurant_name"
    }

    fun put(key: String, value: String?) {
        sharedPrefs?.edit()?.putString(key, value)?.apply()
    }

    fun put(key: String, value: Int) {
        sharedPrefs?.edit()?.putInt(key, value)?.apply()
    }

    fun get(key: String, defaultValue: String?): String? {
        return sharedPrefs?.getString(key, defaultValue)
    }

    fun get(key: String, defaultValue: Int): Int? {
        return sharedPrefs?.getInt(key, defaultValue)
    }

    fun remove(key: String) {
        sharedPrefs?.edit()?.remove(key)?.apply()
    }
}