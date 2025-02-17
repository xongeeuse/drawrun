package com.example.drawrun.data.api

import com.example.drawrun.data.dto.request.course.BookmarkRequest
import com.example.drawrun.data.dto.request.course.CourseSaveRequest
import com.example.drawrun.data.dto.response.course.CourseDetailsResponse
import com.example.drawrun.data.dto.response.course.CourseSaveResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CourseApi {
    @POST("course/save")
    suspend fun saveCourse(@Body request: CourseSaveRequest): Int

    @POST("course/bookmark")
    suspend fun bookmarkCourse(@Body request: BookmarkRequest): Int

    @POST("course/bookmark/cancle")
    suspend fun unbookmarkCourse(@Body request: BookmarkRequest): Int

    @GET("course/search/{courseId}")
    suspend fun getCourseDetails(@Path("courseId") courseId: Int): Response<CourseDetailsResponse>
}