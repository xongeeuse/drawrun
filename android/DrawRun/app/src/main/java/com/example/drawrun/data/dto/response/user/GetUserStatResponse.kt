package com.example.drawrun.data.dto.response.user

data class GetUserStatResponse(
    val isSuccess: Boolean,
    val message: String,
    val code: Int,
    val data: UserStatData
)

data class UserStatData(
    val totalDistanceKm: Double,
    val totalTimeS: Int,
    val averageHeartbeat: Double,
    val averagePaceS: Double,
    val averageCadence: Double,
    val longestStreak: Int,
    val currentStreak: Int
)