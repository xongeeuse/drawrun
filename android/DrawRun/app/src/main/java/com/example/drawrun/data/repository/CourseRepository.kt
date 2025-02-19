package com.example.drawrun.data.repository

import android.util.Log
import com.example.drawrun.data.api.CourseApi
import com.example.drawrun.data.dto.request.course.BookmarkRequest
import com.example.drawrun.data.dto.request.course.CourseSaveRequest
import com.example.drawrun.data.dto.response.course.CourseDetailsResponse
import com.example.drawrun.data.dto.response.course.CourseSaveResponse

// 러닝 코스 저장용 레포지토리
class CourseRepository (private val api: CourseApi) {
    suspend fun saveCourse(request: CourseSaveRequest): Int {
        return api.saveCourse(request)
    }

    fun Int.isBookmarkSuccess(): Boolean = this == 1

    suspend fun bookmarkCourse(request: BookmarkRequest): Boolean {
        return api.bookmarkCourse(request).isBookmarkSuccess()
    }

    suspend fun unbookmarkCourse(request: BookmarkRequest): Boolean {
        return api.unbookmarkCourse(request).isBookmarkSuccess()
    }

    suspend fun fetchCourseDetails(courseId: Int): CourseDetailsResponse? {
        return runCatching {
            val response = api.getCourseDetails(courseId) // ✅ Response<CourseDetailsResponse> 가져오기
            if (response.isSuccessful) {
                response.body() // ✅ Response 객체에서 body()를 꺼내서 반환
            } else {
                Log.e("CourseRepository", "API request failed. Code: ${response.code()}, Message: ${response.message()}")
                null
            }
        }.getOrElse { exception ->
            Log.e("CourseRepository", "Error fetching details", exception)
            null
        }
    }


}
