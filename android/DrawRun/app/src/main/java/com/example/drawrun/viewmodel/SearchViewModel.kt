package com.example.drawrun.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drawrun.data.dto.request.course.BookmarkRequest
import com.example.drawrun.data.repository.SearchRepository
import com.example.drawrun.data.dto.response.search.CourseData
import com.example.drawrun.data.repository.CourseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchRepository: SearchRepository,
    private val courseRepository: CourseRepository
) : ViewModel() {
    // 검색 결과를 저장할 StateFlow
    private val _searchResults = MutableStateFlow<List<CourseData>>(emptyList())
    val searchResults: StateFlow<List<CourseData>> = _searchResults

    // 검색 상태를 관리할 StateFlow
    private val _searchState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchState: StateFlow<SearchState> = _searchState

    // 검색 함수
    fun search(query: String, isKeywordMode: Boolean) {
        viewModelScope.launch {
            Log.d("SearchSearch", "Starting search for query: $query")
            _searchState.value = SearchState.Loading
            val result = if (isKeywordMode) {
                searchRepository.searchByKeyword(query)
            } else {
                searchRepository.searchByLocation(query)
            }
            result.fold(
                onSuccess = { response ->
                    if (response.isEmpty()) {
                        _searchState.value = SearchState.Empty
                    } else {
                        _searchResults.value = response
                        _searchState.value = SearchState.Success
                    }
                },
                onFailure = {
                    _searchState.value = SearchState.Error(it.message ?: "Unknown error occurred")
                }
            )
        }
    }

    // 북마크 토글 함수 추가
    fun toggleBookmark(course: CourseData) {
        viewModelScope.launch {
            try {
                _searchState.value = SearchState.Loading
                val request = BookmarkRequest(course.courseId)
                val isSuccess = if (course.isBookmark) {
                    courseRepository.unbookmarkCourse(request)
                } else {
                    courseRepository.bookmarkCourse(request)
                }
                if (isSuccess) {
                    updateCourseBookmarkStatus(course.courseId, !course.isBookmark)
                    _searchState.value = SearchState.Success
                } else {
                    _searchState.value = SearchState.Error("북마크 변경 실패")
                }
            } catch (e: Exception) {
                _searchState.value = SearchState.Error(e.message ?: "알 수 없는 오류 발생")
            }
        }
    }

    private fun updateCourseBookmarkStatus(courseId: Int, isBookmarked: Boolean) {
        val updatedList = _searchResults.value.map { course ->
            if (course.courseId == courseId) {
                course.copy(
                    isBookmark = isBookmarked,
                    bookmarkCount = course.bookmarkCount + if (isBookmarked) 1 else -1
                )
            } else {
                course
            }
        }
        _searchResults.value = updatedList
    }
}

// 검색 상태를 나타내는 sealed class
sealed class SearchState {
    object Initial : SearchState()
    object Loading : SearchState()
    object Success : SearchState()
    object Empty : SearchState()
    data class Error(val message: String) : SearchState()
}
