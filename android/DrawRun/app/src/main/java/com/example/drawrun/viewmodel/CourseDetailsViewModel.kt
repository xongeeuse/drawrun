package com.example.drawrun.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drawrun.data.dto.response.course.CourseDetailsResponse
import com.example.drawrun.data.repository.CourseRepository
import kotlinx.coroutines.launch

class CourseDetailsViewModel(
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val _courseDetails = MutableLiveData<Result<CourseDetailsResponse>>()
    val courseDetails: LiveData<Result<CourseDetailsResponse>> = _courseDetails

    fun fetchCourseDetails(userPathId: Int) {
        viewModelScope.launch {
            try {
                val response = courseRepository.fetchCourseDetails(userPathId)
                if (response != null) {
                    _courseDetails.value = Result.success(response)
                    Log.d("CourseDetailsViewModel", "Course details fetched: $response")
                } else {
                    _courseDetails.value = Result.failure(Exception("코스 정보를 불러오지 못했습니다."))
                    Log.e("CourseDetailsViewModel", "Failed to fetch course details")
                }
            } catch (e: Exception) {
                _courseDetails.value = Result.failure(e)
                Log.e("CourseDetailsViewModel", "Error fetching course details", e)
            }
        }
    }
}
