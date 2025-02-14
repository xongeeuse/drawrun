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

    val heartRate: StateFlow<Float?> = sensorManagerHelper.heartRateFlow
    private val _isNavigationRunning = MutableStateFlow(false)  // ✅ 네비게이션 실행 여부 저장
    val isNavigationRunning: StateFlow<Boolean> = _isNavigationRunning
    // ✅ 네비게이션 중의 심박수 저장용 리스트
    private val _heartRateListDuringNavigation = mutableListOf<Float>()

    fun startMeasurement() {
        if (_isRunning.value) return
        _isRunning.value = true
        sensorManagerHelper.startSensors()
        Log.d("SensorViewModel", "📡 센서 측정 시작")

        viewModelScope.launch {
            while (_isRunning.value) {
                delay(1000)
                _elapsedTime.update { it + 1 }
                updatePaceAndCadence()
                Log.d("SensorViewModel", "⏳ 네비 상태 체크: _isNavigationRunning = ${_isNavigationRunning.value}")
                saveHeartRateDuringNavigation()
                val currentHeartRate = heartRate.value
                if (_isNavigationRunning.value) {  // ✅ 네비 실행 중일 때만 저장
                    if (currentHeartRate != null && currentHeartRate > 0) {
                        _heartRateListDuringNavigation.add(currentHeartRate)
                        heartRateSum.value += currentHeartRate
                        heartRateCount.value += 1
                        Log.d("SensorViewModel", "💓 [네비 중] 심박수 저장: $currentHeartRate BPM (총 ${heartRateCount.value}회 측정)")
                    } else {
                        Log.w("SensorViewModel", "🚨 [네비 중] 심박수 값이 null 또는 0이라 저장되지 않음")
                    }
                } else {
                    Log.d("SensorViewModel", "🛑 [일반 모드] 네비게이션 실행 중이 아니므로 심박수 저장 안함")
                }
            }
        }
    }

    fun stopMeasurement() {
        _isRunning.value = false
        sensorManagerHelper.stopSensors()
        timerJob?.cancel()
        timerJob = null
        _isNavigationRunning.value = false
        Log.d("SensorViewModel", "🛑 네비게이션 종료됨 - 심박수 저장 중단")
        Log.d("SensorViewModel", "⏳ 네비 상태 체크 (종료 후): _isNavigationRunning = ${_isNavigationRunning.value}")
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

    fun getAverageHeartRateDuringNavigation(): Float {
        return if (_heartRateListDuringNavigation.isNotEmpty()) {
            _heartRateListDuringNavigation.average().toFloat()
        } else {
            Log.e("SensorViewModel", "🚨 네비게이션 중 저장된 심박수가 없음")
            0f
        }
    }


    fun getAverageHeartRate(): Float {
        return if (heartRateCount.value > 0) {
            heartRateSum.value / heartRateCount.value
        } else 0f
    }

    fun startNavigation() {
        if (!_isNavigationRunning.value) {
            _isNavigationRunning.value = true  // ✅ 네비게이션 시작!
            _heartRateListDuringNavigation.clear()  // ✅ 기존 데이터 초기화
            Log.d("SensorViewModel", "🚀 네비게이션 시작 - 심박수 저장 활성화 _isNavigationRunning 업데이트: ${_isNavigationRunning.value}")
        } else {
            Log.d("SensorViewModel", "⚠️ 이미 네비게이션이 실행 중입니다.")
        }
    }
    fun stopNavigation() {
        if (_isNavigationRunning.value) {
            _isNavigationRunning.value = false  // ✅ 네비 종료 처리
            Log.d("SensorViewModel", "🛑 네비게이션 종료 - 심박수 저장 중단 _isNavigationRunning 업데이트: ${_isNavigationRunning.value}")
        } else {
            Log.d("SensorViewModel", "⚠️ 네비게이션이 이미 종료된 상태입니다.")
        }
    }

    fun saveHeartRateDuringNavigation() {
        val currentHeartRate = heartRate.value
        if (_isNavigationRunning.value && currentHeartRate != null && currentHeartRate > 0) {
            _heartRateListDuringNavigation.add(currentHeartRate)
            Log.d("SensorViewModel", "💓 [네비 중] 심박수 저장: $currentHeartRate BPM")
        }
    }

}

