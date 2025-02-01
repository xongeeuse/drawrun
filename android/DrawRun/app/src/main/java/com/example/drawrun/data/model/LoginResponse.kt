package com.example.drawrun.data.model

data class LoginResponse(
    val isSuccess: Boolean,  // 요청 성공 여부
    val message: String,  // 응답 메시지 (예: "로그인 성공")
    val code: Int,  // 응답 코드 (예: 200)
    val data: TokenData?  // 액세스 토큰을 담는 데이터 객체
)

// 응답 데이터 중 accessToken만 포함하는 클래스
data class TokenData(
    val accessToken: String
)