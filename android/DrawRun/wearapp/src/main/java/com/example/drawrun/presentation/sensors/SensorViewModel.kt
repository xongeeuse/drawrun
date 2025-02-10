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
    fun startMeasurement() {
        if (_isRunning.value) return // 이미 실행 중이면 중복 실행 방지
        _isRunning.value = true
        sensorManagerHelper.startSensors()

        // 타이머 동작
        viewModelScope.launch {
            while (_isRunning.value) {
                delay(1000)
                _elapsedTime.update { it + 1 }
                updatePaceAndCadence()
                Log.d("SensorViewModel", "Elapsed time: $elapsedTime, Step count: ${stepCount.value}")
            }
        }
        // 심박수 데이터를 수집하여 평균을 계산하기 위한 로직 추가
        viewModelScope.launch {
            sensorManagerHelper.heartRateFlow.collect { heartRate ->
                if (heartRate != null && heartRate > 0f) {
                    updateHeartRate(heartRate)  // 심박수 누적
                    Log.d("SensorViewModel", "Heart rate collected: $heartRate BPM")
                }
            }
        }

    }

    fun stopMeasurement() {
        _isRunning.value = false
        sensorManagerHelper.stopSensors()
        timerJob?.cancel()
        timerJob = null

        val averageHeartRate = getAverageHeartRate()
        Log.d("SensorViewModel", "평균 심박수 : $averageHeartRate BPM")
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

    fun updateHeartRate(heartRate: Float) {
        heartRateSum.value += heartRate
        heartRateCount.value += 1
    }

    fun getAverageHeartRate(): Float {
        return if (heartRateCount.value > 0) {
            heartRateSum.value / heartRateCount.value
        } else 0f
    }

}

