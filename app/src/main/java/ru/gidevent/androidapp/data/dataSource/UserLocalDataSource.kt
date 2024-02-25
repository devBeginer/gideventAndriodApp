package ru.gidevent.androidapp.data.dataSource

import android.content.SharedPreferences
import ru.gidevent.androidapp.utils.SharedPreferencesUtils.editPref
import javax.inject.Inject

class UserLocalDataSource @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    companion object{
        const val ACCESS_TOKEN = "accessToken"
        const val REFRESH_TOKEN = "refreshToken"
    }

    fun getAccessTokenFromSP(): String {
        return sharedPreferences.getString(ACCESS_TOKEN, "") ?: ""
    }

    fun saveAccessTokenToSP(token: String) {
        return sharedPreferences.editPref(ACCESS_TOKEN, token)
    }

    fun getRefreshTokenFromSP(): String {
        return sharedPreferences.getString(REFRESH_TOKEN, "") ?: ""
    }

    fun saveRefreshTokenToSP(token: String) {
        return sharedPreferences.editPref(REFRESH_TOKEN, token)
    }
}