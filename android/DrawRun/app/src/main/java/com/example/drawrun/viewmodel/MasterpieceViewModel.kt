package com.example.drawrun.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drawrun.data.dto.request.masterpiece.MasterpieceSaveRequest
import com.example.drawrun.data.repository.MasterpieceRepository
import kotlinx.coroutines.launch

class MasterpieceViewModel(private val repository: MasterpieceRepository) : ViewModel() {
    private val _saveMasterpieceResult = MutableLiveData<Result<Int>>()
    val saveMasterpieceResult: LiveData<Result<Int>> = _saveMasterpieceResult

    fun saveMasterpiece(request: MasterpieceSaveRequest) {
        viewModelScope.launch {
            try {
                Log.d("MasterpieceViewModel", "Saving masterpiece: $request")
                val result = repository.saveMasterpiece(request)
                _saveMasterpieceResult.value = Result.success(result)
            } catch (e: Exception) {
                Log.e("MasterpieceViewModel", "Error saving masterpiece", e)
                _saveMasterpieceResult.value = Result.failure(e)
            }
        }
    }
}