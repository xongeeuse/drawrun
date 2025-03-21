package com.example.drawrun.presentation.sensors

import android.content.Context
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

class SensorManagerHelper(private val context: Context) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    // 상태 플래그 추가 (센서 동작 여부)
    private var isSensorRunning = false

    // 심박수, 가속도, GPS 데이터 Flow
    val heartRateFlow = MutableStateFlow<Float?>(null)
    val accelerometerFlow = MutableStateFlow<List<Float>?>(null)
    val paceFlow = MutableStateFlow<Float?>(null)
    val cadenceFlow = MutableStateFlow<Int?>(null)

    private var lastLocation: Location? = null
    var totalDistance: Float = 0f
    val stepCountFlow = MutableStateFlow(0)
    val totalDistanceFlow = MutableStateFlow(0f)
    var elapsedTime: Int = 0
    private val heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
    private val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

    init {
        val sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL)
        sensorList.forEach {
//            Log.d("SensorManagerHelper", "Available sensor: ${it.name}")
        }
    }

    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                when (it.sensor.type) {
                    Sensor.TYPE_HEART_RATE -> {
                        val heartRate = event.values[0]
                        if (heartRate > 0) {
                            heartRateFlow.value = heartRate
                            Log.d("SensorManagerHelper", "💓 심박수 감지됨: $heartRate BPM")
                        } else {
                            Log.w("SensorManagerHelper", "🚨 심박수 값이 0 또는 유효하지 않음: $heartRate")
                        }
                    }
                    else -> {}
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            Log.d("SensorManagerHelper", "⚠️ 센서 정확도 변경됨: ${sensor?.name}, 정확도: $accuracy")
        }
    }
    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val minDistanceThreshold = 0.5f
            if (lastLocation == null) {
                lastLocation = location
            } else {
                val distance = lastLocation!!.distanceTo(location)
                if (distance > minDistanceThreshold) {
                    val newTotalDistance = totalDistanceFlow.value + distance
                    totalDistanceFlow.value = newTotalDistance
                    lastLocation = location
                    Log.d("SensorManagerHelper", "Distance moved: $distance meters")
                }
                paceFlow.value = if (elapsedTime > 0 && totalDistance > 0) {
                    (elapsedTime / 60f) / (totalDistance / 1000f)
                } else null
            }
        }

        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    /**
     * 센서 및 위치 업데이트 시작
     */
    fun startSensors() {
        if (isSensorRunning) {
            Log.d("SensorManagerHelper", "Sensors are already running")
            return
        }

        isSensorRunning = true
        Log.d("SensorManagerHelper", "✅ 센서 시작됨")

        heartRateSensor?.let {
            sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        accelerometerSensor?.let {
            sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        stepDetectorSensor?.let {
            sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d("SensorManagerHelper", "Step Detector sensor registered successfully")
        } ?: Log.e("SensorManagerHelper", "Step detector sensor not available")

        heartRateSensor?.let {
            sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d("SensorManagerHelper", "💓 심박수 센서 등록 완료")
        }
        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000L,
                1f,
                locationListener
            )
//            Log.d("SensorManagerHelper", "Location updates started successfully")
        } catch (e: SecurityException) {
//            Log.e("SensorManagerHelper", "Location permission not granted : ${e.message}")
        }
    }

    /**
     * 센서 및 위치 업데이트 중지
     */
    fun stopSensors() {
        if (!isSensorRunning) {
//            Log.d("SensorManagerHelper", "Sensors are not running")
            return
        }

        isSensorRunning = false
//        Log.d("SensorManagerHelper", "Stopping sensors...")

        sensorManager.unregisterListener(sensorEventListener)
        locationManager.removeUpdates(locationListener)
//        Log.d("SensorManagerHelper", "Sensors stopped successfully")
    }

    fun calculateCadence(elapsedTimeInSeconds: Int): Float? {
        if (elapsedTimeInSeconds > 0) {
            val elapsedTimeInMinutes = elapsedTimeInSeconds / 60f
            return if (stepCountFlow.value > 0) stepCountFlow.value / elapsedTimeInMinutes else 0f
        }
        return null
    }

    fun calculatePace(elapsedTimeInSeconds: Int): Float? {
        if (totalDistance > 0 && elapsedTimeInSeconds > 0) {
            val elapsedTimeInMinutes = elapsedTimeInSeconds / 60f
            val distanceInKm = totalDistance / 1000f
            return elapsedTimeInMinutes / distanceInKm
        }
        return null
    }

    fun calculateCalories(elapsedTime: Int, averageHeartRate: Float, weightKg: Float, age: Int): Float {
        if (elapsedTime <= 0 || weightKg <= 0 || averageHeartRate <= 0) return 0f

        val maxHeartRate = 220 - age
        val mets = (averageHeartRate / maxHeartRate) * 10

        val minutes = elapsedTime / 60f
        return mets * weightKg * minutes
    }

    fun resetData() {
        totalDistance = 0f
        stepCountFlow.value = 0
        heartRateFlow.value = null
        accelerometerFlow.value = null
        cadenceFlow.value = null
        paceFlow.value = null
        totalDistanceFlow.value = 0f
    }
}
