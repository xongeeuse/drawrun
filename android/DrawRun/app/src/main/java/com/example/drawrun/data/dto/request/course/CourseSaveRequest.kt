package com.example.drawrun.data.dto.request.course

// 코스 이미지 저장 API와 분리해야 함
data class CourseSaveRequest(
//    val userId: String,
    val path: List<Point>,
    val pathImgUrl: String,
    val name: String,
    val distance: Double
)

// 경유지 데이터 클래스
data class Point(
    val longitude: Double,
    val latitude: Double,
)
