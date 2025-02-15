package com.example.drawrun.data.api

import com.example.drawrun.data.dto.request.runrecord.RunRecordRequest
import com.example.drawrun.data.dto.response.runrecord.RunRecordResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RunRecordApi {
    @POST("course/courseresultsave")
    suspend fun saveRunRecord(@Body request: RunRecordRequest): Response<RunRecordResponse>
}
