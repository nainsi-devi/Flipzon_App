package com.example.flipzon.data.local

import android.content.Context
import android.content.SharedPreferences

class UserPrefs(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveUser(id: Int, email: String, firstName: String, lastName: String, image: String) {
        prefs.edit()
            .putInt("user_id", id)
            .putString("email", email)
            .putString("first_name", firstName)
            .putString("last_name", lastName)
            .putString("image", image)
            .apply()
    }

    fun getUserId(): Int = prefs.getInt("user_id", -1)

    fun getEmail(): String = prefs.getString("email", "") ?: ""

    fun getFirstName(): String = prefs.getString("first_name", "") ?: ""

    fun getLastName(): String = prefs.getString("last_name", "") ?: ""

    fun getFullName(): String = "${getFirstName()} ${getLastName()}"

    fun getImage(): String = prefs.getString("image", "") ?: ""

    fun isLoggedIn(): Boolean = getUserId() != -1

    fun clear() {
        prefs.edit().clear().apply()
    }
}
