package com.example.drawrun.data.api

import com.example.drawrun.data.dto.request.course.CourseSaveRequest
import com.example.drawrun.data.dto.response.course.CourseSaveResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface CourseApi {
    @POST("course/save")
    suspend fun saveCourse(@Body request: CourseSaveRequest): CourseSaveResponse
}