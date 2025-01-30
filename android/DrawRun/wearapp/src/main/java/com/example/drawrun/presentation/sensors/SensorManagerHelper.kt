package com.example.drawrun.presentation.sensors

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.math.sqrt

class SensorManagerHelper(private val context: Context) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    // 심박수, 가속도, GPS 데이터 Flow
    val heartRateFlow = MutableStateFlow<Float?>(null)
    val accelerometerFlow = MutableStateFlow<List<Float>?>(null)
    val paceFlow = MutableStateFlow<Float?>(null) // 페이스
    val cadenceFlow = MutableStateFlow<Int?>(null) // 케이던스

    private var lastLocation: Location? = null // 이전 GPS 위치
    private var totalDistance: Float = 0f // 총 이동 거리
    private var stepCount: Int = 0 // 걸음 수

    var elapsedTime: Int = 0 // 경과 시간 (초)
    private val heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
    private val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                when (it.sensor.type) {
                    Sensor.TYPE_HEART_RATE -> {
                        heartRateFlow.value = it.values[0]
                        Log.d("SensorManagerHelper", "Heart rate data: ${it.values[0]}")
                    }
                    Sensor.TYPE_ACCELEROMETER -> {
                        val x = it.values[0]
                        val y = it.values[1]
                        val z = it.values[2]
                        val magnitude = sqrt(x * x + y * y + z * z) // 가속도 벡터 크기

                        accelerometerFlow.value = listOf(x, y, z)

                        // 간단한 걸음 수 계산 (패턴 기반)
                        if (magnitude > 10) { // 특정 임계값 이상일 때 걸음으로 간주
                            stepCount++
                            if (elapsedTime > 0) { // elapsedTime이 0인지 확인
                                cadenceFlow.value = (stepCount * 60) / elapsedTime
                            } else {
                                cadenceFlow.value = null // 아직 경과 시간이 없을 경우 null로 설정
                            }
                        } else {
                            Log.d("SensorManagerHelper", "Magnitude below threshold: $magnitude")
                        }
                    }
                    else -> {
                        Log.d("SensorManagerHelper", "Unhandled sensor type: ${it.sensor.type}")
                    }
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            Log.d("SensorManagerHelper", "Accuracy changed for sensor: ${sensor?.name}, accuracy: $accuracy")
        }
    }


    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            if (lastLocation == null) {
                lastLocation = location // 최초 위치 초기화
            } else {
                val distance = lastLocation!!.distanceTo(location)
                totalDistance += distance
                paceFlow.value = if (elapsedTime > 0 && totalDistance > 0) {
                    (elapsedTime / 60f) / (totalDistance / 1000f)
                } else {
                    0f
                }
                lastLocation = location
            }
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }


    /**
     * 센서 및 위치 업데이트 시작
     */
    fun startSensors() {

        heartRateSensor?.let {
            sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        accelerometerSensor?.let {
            sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000L, // 1초마다 업데이트
                1f,    // 최소 1미터 이동 시 업데이트
                locationListener
            )
        } catch (e: SecurityException) {
            Log.e("SensorManagerHelper", "Location permission not granted")
        }
    }

    /**
     * 센서 및 위치 업데이트 중지
     */
    fun stopSensors() {
        sensorManager.unregisterListener(sensorEventListener)
        locationManager.removeUpdates(locationListener)
        Log.d("SensorManagerHelper", "Sensors stopped")
    }
    fun calculateCadence(elapsedTime: Int): Float? {
        return if (elapsedTime > 0) (stepCount * 60) / elapsedTime.toFloat() else null
    }

    fun calculatePace(elapsedTime: Int): Float? {
        return if (totalDistance > 0) (elapsedTime / (totalDistance / 1000f)) else null
    }

    fun calculateCalories(elapsedTime: Int, averageHeartRate: Float, weightKg: Float, age: Int): Float {
        if (elapsedTime <= 0 || weightKg <= 0 || averageHeartRate <= 0) return 0f

        val maxHeartRate = 220 - age
        val mets = (averageHeartRate / maxHeartRate) * 10

        val minutes = elapsedTime / 60f
        return mets * weightKg * minutes
    }

    
    // 데이터 초기화 메서드
    fun resetData() {
        totalDistance = 0f
        stepCount = 0
        heartRateFlow.value = null
        accelerometerFlow.value = null
        cadenceFlow.value = null
        paceFlow.value = null
    }

}
