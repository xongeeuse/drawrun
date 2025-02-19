package com.example.drawrun.data.api

import com.example.drawrun.data.dto.request.course.AiCourseRequest
import com.example.drawrun.data.dto.response.course.AiCourseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AiCourseApi {
    @POST("ai/make")
    suspend fun requestAiCourse(@Body requestBody: AiCourseRequest): Response<AiCourseResponse>
}