package com.example.drawrun.data.dto.request.masterpiece

import java.time.LocalDate

data class MasterpieceSaveRequest(
    val userPathId: Int,
    val paths: List<List<Point>>,
    val restrictCount: Int,
    val expireDate: LocalDate
)

data class Point(
    val longitude: Double,
    val latitude: Double,
)