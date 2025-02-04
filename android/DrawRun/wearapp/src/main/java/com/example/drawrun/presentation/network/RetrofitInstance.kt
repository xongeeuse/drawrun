package com.example.drawrun.presentation.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api: StatsApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://your-server-url.com/api/")  // 서버 URL 설정
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StatsApi::class.java)
    }
}
