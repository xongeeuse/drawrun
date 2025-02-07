package com.example.drawrun.data.repository

import com.example.drawrun.data.api.CourseApi
import com.example.drawrun.data.dto.request.course.CourseSaveRequest
import com.example.drawrun.data.dto.response.course.CourseSaveResponse

// 러닝 코스 저장용 레포지토리
class CourseRepository (private val api: CourseApi) {
    suspend fun saveCourse(request: CourseSaveRequest): CourseSaveResponse {
        return api.saveCourse(request)
    }
}
