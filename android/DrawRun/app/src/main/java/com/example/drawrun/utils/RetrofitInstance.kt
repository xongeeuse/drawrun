package com.example.drawrun.utils

import android.content.Context
import com.example.drawrun.data.api.AuthApi
import com.example.drawrun.data.api.CourseApi
import com.example.drawrun.data.api.ImageUploadApi
import com.example.drawrun.data.api.MasterpieceApi
import com.example.drawrun.data.api.SearchApi
import com.example.drawrun.data.api.UserApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://13.124.222.21:8081/api/v1/"

    val api: AuthApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }

    // OkHttp 클라이언트 생성 (토큰 필요)
    private fun getOkHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .build()
    }

    // Retrofit 인스턴스 생성 (토큰 필요)
    private fun getRetrofit(context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getOkHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun UserApi(context: Context): UserApi = getRetrofit(context).create(UserApi::class.java)
    fun SearchApi(context: Context): SearchApi = getRetrofit(context).create(SearchApi::class.java)
    fun CourseApi(context: Context): CourseApi = getRetrofit(context).create(CourseApi::class.java)
    fun ImageUploadApi(context: Context): ImageUploadApi = getRetrofit(context).create(ImageUploadApi::class.java)
    fun MasterpieceApi(context: Context): MasterpieceApi = getRetrofit(context).create(MasterpieceApi::class.java)

}
