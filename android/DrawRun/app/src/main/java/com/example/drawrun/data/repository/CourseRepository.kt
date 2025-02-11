package com.example.drawrun.data.repository

import com.example.drawrun.data.api.CourseApi
import com.example.drawrun.data.dto.request.course.BookmarkRequest
import com.example.drawrun.data.dto.request.course.CourseSaveRequest
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
}
