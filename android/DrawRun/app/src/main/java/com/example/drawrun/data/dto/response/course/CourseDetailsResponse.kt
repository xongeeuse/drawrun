package com.example.drawrun.data.dto.response.course

data class CourseDetailsResponse(
    val path: List<LatLngData>,
    val location: String,
    val userPathId: Int,
    val distance: Double
)

data class LatLngData(
    val latitude: Double,
    val longitude: Double
)