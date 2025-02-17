package com.example.drawrun.data.dto.request.masterpiece

data class MasterpieceSaveRequest(
    val userPathId: Int,
    val paths: List<List<Point>>,
    val restrictCount: Int,
    val expireDate: String
)

data class Point(
    val longitude: Double,
    val latitude: Double,
)