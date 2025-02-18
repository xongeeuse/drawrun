package com.example.drawrun.data.dto.response.course

data class AiCourseResponse(
    val isSuccess: Boolean,
    val message: String,
    val code: Int,
    val data: AiCourseData?
)

data class AiCourseData(
    val path: List<PathPoint>
)

data class PathPoint(
    val latitude: Double,
    val longitude: Double
)