package com.example.drawrun.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.drawrun.data.repository.CourseRepository

class CourseViewModelFactory(private val courseRepository: CourseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CourseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CourseViewModel(courseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
