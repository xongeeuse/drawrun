package com.example.drawrun.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.drawrun.data.repository.CourseRepository
import com.example.drawrun.data.repository.SearchRepository

class SearchViewModelFactory(
    private val searchRepository: SearchRepository,
    private val courseRepository: CourseRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(searchRepository, courseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
