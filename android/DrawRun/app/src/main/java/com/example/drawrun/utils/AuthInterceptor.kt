package com.example.drawrun.utils

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response


/*
*   API 요청 시 토큰을 자동으로 헤더에 포함하는 파일
*   OKHttp Interceptor 사용
*   언니들도 필요하면 쓰도록 ❤️
* */
class AuthInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // 토큰 가져오기
        val token = SecureStorage.getAccessToken(context)

        // 토큰이 존재하면 Authorization 헤더에 추가
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}
