package com.example.drawrun.data.repository

import com.example.drawrun.data.api.AuthApi
import com.example.drawrun.data.model.AuthRequest
import com.example.drawrun.data.model.AuthResponse

class AuthRepository(private val api: AuthApi) {
    suspend fun register(request: AuthRequest): AuthResponse {
        return api.register(request)
    }
}
