package ru.gidevent.androidapp.utils

import android.content.SharedPreferences

object SharedPreferencesUtils {
    fun SharedPreferences.editPref(key: String?, value: String?) {
        val editor: SharedPreferences.Editor = this.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun SharedPreferences.editPref(key: String?, value: Boolean) {
        val editor: SharedPreferences.Editor = this.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun SharedPreferences.editPref(key: String?, value: Int) {
        val editor: SharedPreferences.Editor = this.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun SharedPreferences.editPref(key: String?, value: Float) {
        val editor: SharedPreferences.Editor = this.edit()
        editor.putFloat(key, value)
        editor.apply()
    }

    fun SharedPreferences.editPref(key: String?, value: Long) {
        val editor: SharedPreferences.Editor = this.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun SharedPreferences.removePref(key: String?) {
        val editor: SharedPreferences.Editor = this.edit()
        editor.remove(key)
        editor.apply()
    }
}