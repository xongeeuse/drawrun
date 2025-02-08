package com.example.drawrun.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drawrun.data.dto.request.course.CourseSaveRequest
import com.example.drawrun.data.dto.request.course.Waypoint
import com.example.drawrun.data.dto.response.course.CourseSaveResponse
import com.example.drawrun.data.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject


@HiltViewModel  // Hilt를 사용한 ViewModel 의존성 주입
class CourseViewModel @Inject constructor(
    private val courseRepository: CourseRepository  // Repository 주입
) : ViewModel() {

    // 코스 저장 결과를 담는 LiveData
    // MutableLiveData는 값을 변경할 수 있는 LiveData
    private val _saveCourseResult = MutableLiveData<Result<CourseSaveResponse>>()

    // 외부에서 관찰 가능한 불변 LiveData
    // Result로 감싸서 성공/실패 여부를 함께 전달
    val saveCourseResult: LiveData<Result<CourseSaveResponse>> = _saveCourseResult

    /**
     * 코스 저장 함수
     * @param userId 사용자 ID
     * @param waypoints 경유지 목록
     * @param courseName 코스 이름
     * @param courseImgUrl 코스 이미지 URL
     * @param distance 총 거리
     */
    fun saveCourse(
        userId: String,
        waypoints: List<Waypoint>,
        courseName: String,
        courseImgUrl: String,
        distance: Double
    ) {
        // 코루틴 스코프에서 비동기 작업 실행
        viewModelScope.launch {
            try {
                // CourseSaveRequest 객체 생성
                val request = CourseSaveRequest(
                    userId = userId,
                    waypoints = waypoints,
                    courseName = courseName,
                    courseImgUrl = courseImgUrl,
                    createDate = LocalDateTime.now().toString(),  // 현재 시간을 생성일로 설정
                    distance = distance
                )

                // Repository를 통해 코스 저장 요청
                val response = courseRepository.saveCourse(request)
                // 성공 결과를 LiveData에 전달
                _saveCourseResult.value = Result.success(response)
            } catch (e: Exception) {
                // 실패 시 에러를 LiveData에 전달
                _saveCourseResult.value = Result.failure(e)
            }
        }
    }
}
