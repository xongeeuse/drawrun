package com.example.drawrun.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drawrun.data.dto.request.runrecord.RunRecordRequest
import com.example.drawrun.data.repository.RunRecordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RunRecordViewModel(private val repository: RunRecordRepository) : ViewModel() {

    private val _saveResult = MutableStateFlow<Result<Unit>?>(null)
    val saveResult: StateFlow<Result<Unit>?> = _saveResult.asStateFlow()

    fun saveRunRecord(request: RunRecordRequest) {
        viewModelScope.launch {
            repository.saveRunRecord(request).collect { result ->
                _saveResult.value = result
            }
        }
    }
}
