package com.example.drawrun.presentation

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DataViewModel : ViewModel() {
    // 🔥 StateFlow로 UI 상태 관리 (초기값 설정)
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

    // ✅ 강제 UI 갱신을 위한 트리거 변수 추가
    private val _updateTrigger = MutableStateFlow(false)
    val updateTrigger: StateFlow<Boolean> get() = _updateTrigger

    // ✅ 목적지 도착 여부 체크
    fun checkDestinationReached(distanceRemaining: Double) {
        _isDestinationReached.value = distanceRemaining <= 0.0
    }

    fun updateData(
        distanceToNextTurn: Double,
        voiceInstruction: String,
        totalDistance: Double,
        distanceRemaining: Double
    ) {
        Log.d("DataViewModel", "🔥 updateData 호출됨: distanceToNextTurn=$distanceToNextTurn, voiceInstruction=$voiceInstruction, totalDistance=$totalDistance, distanceRemaining=$distanceRemaining")

        viewModelScope.launch {
            _distanceToNextTurn.value = distanceToNextTurn
            _voiceInstruction.value = voiceInstruction
            _totalDistance.value = totalDistance
            _distanceRemaining.value = distanceRemaining

            Log.d("DataViewModel", "✅ 데이터 변경 완료: distanceToNextTurn=${_distanceToNextTurn.value}, voiceInstruction=${_voiceInstruction.value}")
        }
    }

}
