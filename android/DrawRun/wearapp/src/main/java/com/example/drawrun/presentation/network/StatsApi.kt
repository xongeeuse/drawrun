package com.example.drawrun.presentation.network

import retrofit2.http.Body
import retrofit2.http.POST

data class StatsData(
    val userId: Int,
    val distanceKm: Float,
    val timeS: Int,
    val paceS: Float,
    val calorie: Float,
    val heartbeat: Int,
    val timestamp: Long
)

interface StatsApi {
    @POST("stats/save")
    suspend fun sendStats(@Body statsData: StatsData)
}
