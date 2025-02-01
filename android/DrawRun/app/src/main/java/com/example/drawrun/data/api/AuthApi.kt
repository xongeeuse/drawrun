package com.example.drawrun.data.api

import com.example.drawrun.data.model.AuthRequest
import com.example.drawrun.data.model.AuthResponse
import com.example.drawrun.data.model.LoginRequest
import com.example.drawrun.data.model.LoginResponse
import retrofit2.http.POST
import retrofit2.http.Body

interface AuthApi {
    @POST("auth/register")
    suspend fun register(@Body request: AuthRequest): AuthResponse

    // 로그인 기능 제공
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

}