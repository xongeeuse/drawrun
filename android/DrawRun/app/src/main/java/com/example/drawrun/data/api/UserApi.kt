package com.example.drawrun.data.api

import retrofit2.Call
import com.example.drawrun.data.model.UserResponse
import retrofit2.http.GET

interface UserApi {
    @GET("user/mypage")
    fun getUserData(): Call<UserResponse>
}