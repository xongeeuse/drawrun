package com.example.drawrun.utils

import android.content.Context

object SecureStorage {

    private const val PREFS_NAME = "secure_prefs"
    private const val ACCESS_TOKEN_KEY = "access_token"

    private fun getSharedPreferences(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // ✅ 토큰 저장 (암호화 제거)
    fun saveAccessToken(context: Context, token: String) {
        getSharedPreferences(context).edit().putString(ACCESS_TOKEN_KEY, token).apply()
    }

    // ✅ 토큰 가져오기 (복호화 제거)
    fun getAccessToken(context: Context): String? {
        return getSharedPreferences(context).getString(ACCESS_TOKEN_KEY, null)
    }

    // ✅ 토큰 삭제
    fun clearAccessToken(context: Context) {
        getSharedPreferences(context).edit().remove(ACCESS_TOKEN_KEY).apply()
    }
}
