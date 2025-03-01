package com.example.asms.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.asms.model.UserData

object PrefsManager {
    private const val PREF_NAME = "ASMS_PREFS"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USERNAME = "username"
    private const val KEY_EMAIL = "email"
    private const val KEY_CREDITS = "credits"
    private const val KEY_DEVICE_ID = "device_id"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveUser(userData: UserData) {
        prefs.edit().apply {
            putInt(KEY_USER_ID, userData.id)
            putString(KEY_USERNAME, userData.username)
            putString(KEY_EMAIL, userData.email)
            putInt(KEY_CREDITS, userData.credits)
            apply()
        }
    }

    fun getUser(): UserData? {
        val id = prefs.getInt(KEY_USER_ID, -1)
        if (id == -1) return null

        return UserData(
            id = id,
            username = prefs.getString(KEY_USERNAME, "") ?: "",
            email = prefs.getString(KEY_EMAIL, "") ?: "",
            credits = prefs.getInt(KEY_CREDITS, 0)
        )
    }

    fun saveDeviceId(deviceId: String) {
        prefs.edit().putString(KEY_DEVICE_ID, deviceId).apply()
    }

    fun getDeviceId(): String? {
        return prefs.getString(KEY_DEVICE_ID, null)
    }

    fun clearUser() {
        prefs.edit().clear().apply()
    }
}