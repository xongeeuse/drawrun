package com.example.drawrun.presentation.sensors

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SensorViewModel(private val sensorManagerHelper: SensorManagerHelper) : ViewModel(),
    DefaultLifecycleObserver {

    val heartRate: StateFlow<Float?> = sensorManagerHelper.heartRateFlow
    val accelerometer: StateFlow<List<Float>?> = sensorManagerHelper.accelerometerFlow
    val cadence: StateFlow<Int?> = sensorManagerHelper.cadenceFlow
    private val _cadence = MutableStateFlow<Int?>(null)
    val pace: StateFlow<Float?> = sensorManagerHelper.paceFlow
    private val _pace = MutableStateFlow<Float?>(null)
    private val _elapsedTime = MutableStateFlow(0)
    val elapsedTime: StateFlow<Int> = _elapsedTime
    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning
    val stepCount = sensorManagerHelper.stepCountFlow
    val totalDistance = sensorManagerHelper.totalDistanceFlow
    private var timerJob: Job? = null  // 타이머 Job 추가


    // ✅ 네비게이션 중의 심박수 저장용 리스트
    private val _heartRateListDuringNavigation = mutableListOf<Float>()

    fun startMeasurement() {
        if (_isRunning.value) return // 이미 실행 중이면 중복 실행 방지
        _isRunning.value = true
        sensorManagerHelper.startSensors()

        _heartRateListDuringNavigation.clear()

        // 타이머 동작
        viewModelScope.launch {
            while (_isRunning.value) {
                delay(1000)
                _elapsedTime.update { it + 1 }
                updatePaceAndCadence()
                Log.d("SensorViewModel", "Elapsed time: $elapsedTime, Step count: ${stepCount.value}")
                // ✅ 네비게이션 중의 심박수 저장
                heartRate.value?.let { _heartRateListDuringNavigation.add(it) }
            }
        }
    }

    fun stopMeasurement() {
        _isRunning.value = false
        sensorManagerHelper.stopSensors()
        timerJob?.cancel()
        timerJob = null
    }

    fun resetMeasurement() {
        _elapsedTime.value = 0
        _cadence.value = null
        _pace.value = null
        sensorManagerHelper.resetData()
    }

    fun calculateCalories(elapsedTime: Int, averageHeartRate: Float, weightKg: Float, age: Int): Float {
        if (!_isRunning.value || elapsedTime <= 0 || weightKg <= 0) return 0f
        return sensorManagerHelper.calculateCalories(elapsedTime, averageHeartRate, weightKg, age)
    }

    fun updatePaceAndCadence() {
        val newCadence = sensorManagerHelper.calculateCadence(elapsedTime.value)
        val newPace = sensorManagerHelper.calculatePace(elapsedTime.value)
        // StateFlow 업데이트
        _cadence.value = newCadence?.toInt()
        _pace.value = newPace

        Log.d("SensorViewModel", "Updated pace: $newPace, cadence: $newCadence")
    }

    fun incrementElapsedTime() {
        _elapsedTime.update { it + 1 }
    }

    private val heartRateSum = MutableStateFlow(0f)
    private val heartRateCount = MutableStateFlow(0)

    // ✅ 네비게이션 동안의 평균 심박수 계산 함수 추가
    fun getAverageHeartRateDuringNavigation(): Float {
        return if (_heartRateListDuringNavigation.isNotEmpty()) {
            _heartRateListDuringNavigation.average().toFloat()
        } else 0f
    }

    fun getAverageHeartRate(): Float {
        return if (heartRateCount.value > 0) {
            heartRateSum.value / heartRateCount.value
        } else 0f
    }


}

