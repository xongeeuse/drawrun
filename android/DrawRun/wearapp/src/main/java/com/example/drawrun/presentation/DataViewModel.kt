package com.example.drawrun.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DataViewModel : ViewModel() {
    private val _distanceToNextTurn = MutableStateFlow(0.0)
    val distanceToNextTurn: StateFlow<Double> get() = _distanceToNextTurn

    private val _voiceInstruction = MutableStateFlow("대기 중")
    val voiceInstruction: StateFlow<String> get() = _voiceInstruction

    private val _totalDistance = MutableStateFlow(0.0)
    val totalDistance: StateFlow<Double> get() = _totalDistance

    private val _distanceRemaining = MutableStateFlow(0.0)
    val distanceRemaining: StateFlow<Double> get() = _distanceRemaining

    private val _isDestinationReached = MutableStateFlow(false)
    val isDestinationReached: StateFlow<Boolean> get() = _isDestinationReached
    // 남은 거리 체크 로직
    fun checkDestinationReached(distanceRemaining: Double) {
        _isDestinationReached.value = distanceRemaining <= 0.0
    }
    // 강제 UI 업데이트 함수
    fun forceRefresh() {
        _distanceToNextTurn.value = _distanceToNextTurn.value
        _voiceInstruction.value = _voiceInstruction.value
        _totalDistance.value = _totalDistance.value
        _distanceRemaining.value = _distanceRemaining.value
    }

    // 데이터를 업데이트하는 함수
    fun updateData(
        distanceToNextTurn: Double,
        voiceInstruction: String,
        totalDistance: Double,
        distanceRemaining: Double
    ) {
        Log.d("DataViewModel", "업데이트: distanceToNextTurn=$distanceToNextTurn, voiceInstruction=$voiceInstruction, totalDistance=$totalDistance, distanceRemaining=$distanceRemaining")
        viewModelScope.launch {
            _distanceToNextTurn.value = distanceToNextTurn
            _voiceInstruction.value = voiceInstruction
            _totalDistance.value = totalDistance
            _distanceRemaining.value = distanceRemaining
            checkDestinationReached(distanceRemaining)
        }
    }
}

