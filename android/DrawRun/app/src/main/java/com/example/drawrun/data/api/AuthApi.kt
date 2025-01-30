package com.example.drawrun.data.api

import com.example.drawrun.data.model.AuthRequest
import com.example.drawrun.data.model.AuthResponse
import retrofit2.http.POST
import retrofit2.http.Body

interface AuthApi {
    @POST("auth/register")
    suspend fun register(@Body request: AuthRequest): AuthResponse
}