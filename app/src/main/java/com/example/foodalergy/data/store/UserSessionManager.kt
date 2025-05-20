package com.example.foodalergy.data.store

import android.content.Context
import android.content.SharedPreferences

class UserSessionManager(context: Context) {

    companion object {
        private const val PREFS_NAME = "user_prefs"
        private const val KEY_USERNAME = "username"
        private const val KEY_TOKEN = "token"
        private const val KEY_USERID = "userId"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveSession(username: String, token: String, userId: String) {
        prefs.edit().apply {
            putString(KEY_USERNAME, username)
            putString(KEY_TOKEN, token)
            putString(KEY_USERID, userId)
            apply()
        }
    }

    fun getUsername(): String = prefs.getString(KEY_USERNAME, "") ?: ""
    fun getToken(): String = prefs.getString(KEY_TOKEN, "") ?: ""
    fun getUserId(): String = prefs.getString(KEY_USERID, "") ?: ""

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean = getToken().isNotEmpty()
}
