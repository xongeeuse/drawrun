package com.example.drawrun.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drawrun.data.repository.SearchRepository
import com.example.drawrun.data.dto.response.search.CourseData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: SearchRepository) : ViewModel() {
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
                repository.searchByKeyword(query)
            } else {
                repository.searchByLocation(query)
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
}

// 검색 상태를 나타내는 sealed class
sealed class SearchState {
    object Initial : SearchState()
    object Loading : SearchState()
    object Success : SearchState()
    object Empty : SearchState()
    data class Error(val message: String) : SearchState()
}
