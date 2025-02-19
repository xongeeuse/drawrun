package com.example.drawrun.data.dto.response.masterpiece

import java.io.Serializable

typealias MasterpieceListResponse = List<Masterpiece>

data class Masterpiece(
    val masterpieceBoardId: Int,   // 게시글 ID
    val userId: Int,              // 작성자 pk
    val restrictCount: Int,       // 제한 인원 수
    val userPathId: Int,          // 경로 ID
    val nickname: String,         // 작성자 닉네임
    val profileImgUrl: String?,   // 프로필 이미지 URL (nullable)
    val pathImgUrl: String?,      // 경로 이미지 URL (nullable)
    val gu: String,               // 구 정보
    val distance: Double,         // 거리 (km)
    val joinCount: Int,           // 현재 참여 인원 수
    val state: Int,             // 완성 여부
    val courseName: String,           // 코스 이름
    val dday: Int                 // D-Day 값
) : Serializable