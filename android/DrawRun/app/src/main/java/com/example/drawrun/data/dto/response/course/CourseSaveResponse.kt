package com.example.drawrun.data.dto.response.course

data class CourseSaveResponse(
    val isSuccess: Boolean,  // 요청 성공 여부
    val message: String,  // 응답 메시지 (예: "로그인 성공")
    val code: Int,  // 응답 코드 (예: 200)
)