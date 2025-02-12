package com.example.drawrun.data.dto.response.image

data class ImageUploadResponse(
    val isSuccess: Boolean, // 업로드 성공 여부
    val message: String,    // 응답 메시지
    val code: Int,          // 상태 코드
    val data: Data?         // 업로드된 파일의 URL 정보 (nullable)
) {
    data class Data(
        val url: String?    // 업로드된 이미지 URL (성공 시 제공)
    )
}