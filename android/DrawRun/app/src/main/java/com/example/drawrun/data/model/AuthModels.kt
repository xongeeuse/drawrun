package com.example.drawrun.data.model

data class AuthRequest(
    val userId: String,
    val email: String,
    val password: String,
    val userName: String,
    val nickname: String
)

data class AuthResponse(
    val isSuccess: Boolean,
    val message: String,
    val code: Int,
    val data: AuthData
)

data class AuthData(
    val status: Boolean
)
