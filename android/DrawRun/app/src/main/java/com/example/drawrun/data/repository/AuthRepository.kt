package com.example.drawrun.data.repository

import com.example.drawrun.data.api.AuthApi
import com.example.drawrun.data.model.AuthRequest
import com.example.drawrun.data.model.AuthResponse
import com.example.drawrun.data.dto.request.auth.LoginRequest
import com.example.drawrun.data.dto.response.auth.LoginResponse


// 회원가입 전용 레포지토리
class AuthRepository(private val api: AuthApi) {
    suspend fun register(request: AuthRequest): AuthResponse {
        return api.register(request)
    }

    // 로그인 요청을 서버로 보냄
    suspend fun login(request: LoginRequest): LoginResponse {
        return api.login(request)
    }
}
