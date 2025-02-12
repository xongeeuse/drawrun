package com.example.drawrun.data.api

import com.example.drawrun.data.dto.request.masterpiece.MasterpieceSaveRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface MasterpieceApi {
    @POST("masterpiece/save")
    suspend fun saveMasterpiece(@Body request: MasterpieceSaveRequest): Int
}