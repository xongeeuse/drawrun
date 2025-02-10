package com.example.drawrun.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drawrun.data.dto.request.course.CourseSaveRequest
import com.example.drawrun.data.dto.request.course.Point
import com.example.drawrun.data.dto.response.course.CourseSaveResponse
import com.example.drawrun.data.repository.CourseRepository
import kotlinx.coroutines.launch

class CourseViewModel(
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val _saveCourseResult = MutableLiveData<Result<CourseSaveResponse>>()
    val saveCourseResult: LiveData<Result<CourseSaveResponse>> = _saveCourseResult

    fun saveCourse(
        path: List<Point>,
        name: String,
        pathImgUrl: String,
        distance: Double
    ) {
        viewModelScope.launch {
            try {
                val request = CourseSaveRequest(
                    path = path,
                    name = name,
                    pathImgUrl = pathImgUrl,
                    distance = distance,
//                    isPublic = isPublic,
                )
                val response = courseRepository.saveCourse(request)
                _saveCourseResult.value = Result.success(response)
            } catch (e: Exception) {
                _saveCourseResult.value = Result.failure(e)
            }
        }
    }
}
