package com.example.drawrun.data.dto.response.masterpiece

data class MasterpieceDetailResponse(
    val masterpieceBoardId: Int,      // 게시글 ID
    val userId: Int,                  // 작성자 ID
    val restrictCount: Int,           // 제한 인원 수
    val userPathId: Int,              // 경로 ID
    val nickname: String,             // 작성자 닉네임
    val profileImgUrl: String?,       // 프로필 이미지 URL (nullable)
    val pathImgUrl: String?,          // 경로 이미지 URL (nullable)
    val gu: String,                   // 지역 정보 (구)
    val distance: Double,             // 거리 (km)
    val joinCount: Int,               // 현재 참여 인원 수
    val courseName: String,           // 코스 이름
    val dday: Int                     // D-Day 값
)
