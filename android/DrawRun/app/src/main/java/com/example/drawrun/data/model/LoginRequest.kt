package com.example.drawrun.data.model

// 로그인 요청을 위한 데이터 클래스
data class LoginRequest(
    val userId: String,
    val password: String
)