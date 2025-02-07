package com.example.drawrun.data.dto.request.course

data class CourseSaveRequest(
    val userId: String,
    val waypoints: List<Waypoint>,
    val courseName: String,
    val courseImgUrl: String,    // Base64 인코딩된 이미지 문자열 또는 이미지 URL
    val createDate: String,     // ISO 8601 형식의 날짜 문자열
    val distance: Double        // km 단위
)

// 경유지 데이터 클래스
data class Waypoint(
    val latitude: Double,
    val longitude: Double
)
