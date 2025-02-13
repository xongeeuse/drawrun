package com.example.drawrun.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.drawrun.data.repository.MasterpieceRepository

class MasterpieceViewModelFactory(private val repository: MasterpieceRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MasterpieceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MasterpieceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}