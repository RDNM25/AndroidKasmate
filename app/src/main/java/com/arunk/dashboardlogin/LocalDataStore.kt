package com.arunk.dashboardlogin

import android.content.Context
import android.content.SharedPreferences

/**
 * Small local store for this single-device app. Account secrets are stored as salted
 * password hashes by UserRepository; financial records are JSON snapshots in private
 * app preferences.
 */
object LocalDataStore {
    private const val FILE_NAME = "kasmate_data"
    private lateinit var preferences: SharedPreferences

    fun initialize(context: Context) {
        preferences = context.applicationContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    }

    fun read(key: String): String? = preferences.getString(key, null)

    fun write(key: String, value: String): Boolean =
        preferences.edit().putString(key, value).commit()
}
