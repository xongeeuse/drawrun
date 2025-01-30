package com.example.drawrun.presentation.sensors

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

class SensorViewModel(private val sensorManagerHelper: SensorManagerHelper) : ViewModel(),
    DefaultLifecycleObserver {
    val heartRate: StateFlow<Float?> = sensorManagerHelper.heartRateFlow
    val accelerometer: StateFlow<List<Float>?> = sensorManagerHelper.accelerometerFlow
    val cadence: StateFlow<Int?> = sensorManagerHelper.cadenceFlow // 케이던스 Flow
    val pace: StateFlow<Float?> = sensorManagerHelper.paceFlow // 페이스 Flow

    // 평균 케이던스를 계산하는 메서드 추가 (SensorManagerHelper의 메서드 호출)
    fun getAverageCadence(elapsedTime: Int): Float? {
        return sensorManagerHelper.calculateCadence(elapsedTime)
    }

    // 평균 페이스를 계산하는 메서드 추가 (SensorManagerHelper의 메서드 호출)
    fun getAveragePace(elapsedTime: Int): Float? {
        return sensorManagerHelper.calculatePace(elapsedTime)
    }

    // 소모 칼로리 계산
    fun calculateCalories(elapsedTime: Int, averageHeartRate: Float, weightKg: Float, age: Int): Float {
        return sensorManagerHelper.calculateCalories(elapsedTime, averageHeartRate, weightKg, age)
    }
    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        startSensors()
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        stopSensors()
    }

    fun startSensors() {
        sensorManagerHelper.startSensors()
    }

    fun stopSensors() {
        sensorManagerHelper.stopSensors()
    }
}
