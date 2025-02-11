package com.example.drawrun.viewmodel

import android.util.Log
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

    private val _saveCourseResult = MutableLiveData<Result<Int>>()
    val saveCourseResult: LiveData<Result<Int>> = _saveCourseResult

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

                // 리퀘스트 내용을 로그로 출력
                Log.d("CourseViewModel", "Save Course Request: $request")

                val response = courseRepository.saveCourse(request)
                if (response != 0) {
                    val courseId = response
                    _saveCourseResult.value = Result.success(courseId)
                    Log.d("CourseViewModel", "Course saved successfully. CourseId: $courseId")
                } else {
                    _saveCourseResult.value = Result.failure(Exception("코스 저장 실패"))
                    Log.e("CourseViewModel", "Failed to save course")
                }
            } catch (e: Exception) {
                _saveCourseResult.value = Result.failure(e)
                Log.e("CourseViewModel", "Error saving course", e)
            }
        }
    }
}
