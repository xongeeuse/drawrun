package com.example.drawrun.presentation

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.drawrun.presentation.sensors.SensorManagerHelper
import com.example.drawrun.presentation.sensors.SensorViewModel
import com.example.drawrun.presentation.sensors.SensorViewModelFactory
import com.example.drawrun.presentation.ui.NavigationScreen
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable

class NavigationActivity : ComponentActivity() {

    private val dataViewModel: DataViewModel by viewModels()
    private lateinit var sensorManagerHelper: SensorManagerHelper
    private val sensorViewModel: SensorViewModel by viewModels {
        SensorViewModelFactory(sensorManagerHelper)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndRequestPermissions()
        sensorManagerHelper = SensorManagerHelper(this)

        registerReceiver(
            navigationUpdateReceiver,
            IntentFilter("com.example.drawrun.presentation.NAVIGATION_UPDATE"),
            Context.RECEIVER_EXPORTED
        )

        sensorViewModel.startNavigation()

        setContent {
            NavigationScreen(dataViewModel, sensorViewModel) {
                val intent = Intent(this, DrawRunMainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private val navigationUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val distanceToNextTurn = it.getDoubleExtra("distanceToNextTurn", 0.0)
                val voiceInstruction = it.getStringExtra("voiceInstruction") ?: "대기 중"
                val totalDistance = it.getDoubleExtra("totalDistance", 0.0)
                val distanceRemaining = it.getDoubleExtra("distanceRemaining", 0.0)

                Log.d("NavigationActivity", "🔥 데이터 수신: distanceToNextTurn=$distanceToNextTurn, voiceInstruction=$voiceInstruction")

                // ✅ 네비게이션 시작 신호를 감지했을 때 실행하도록 추가
                if (distanceRemaining > 5.0 && !sensorViewModel.isNavigationRunning.value) {
                    sensorViewModel.startNavigation() // ✅ 네비 시작!
                    Log.d("NavigationActivity", "🚀 네비게이션 시작 감지 - SensorViewModel에 반영됨")
                }

                // ✅ 남은 거리가 5m 이하라면 목적지 도착으로 판단
                if (distanceRemaining <= 5.0) {
                    Log.d("NavigationActivity", "🚀 목적지 도착 감지 - RunRecordActivity 이동")
                    stopNavigationAndFinish()
                    sensorViewModel.stopNavigation()
                    finish()  // ✅ 현재 액티비티 종료
                }

                dataViewModel.updateData(distanceToNextTurn, voiceInstruction, totalDistance, distanceRemaining)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(navigationUpdateReceiver)

        // ✅ 네비 종료 시 평균 심박수 계산 후 전송
        sensorViewModel.stopNavigation()
//        sendAverageHeartRateToMobile()
    }

    // ✅ 네비 종료 + 평균 심박수 전송 후 액티비티 종료
    private fun stopNavigationAndFinish() {
        if (sensorViewModel.isNavigationRunning.value) { // 네비 실행 중일 때만 실행
            Log.d("NavigationActivity", "🛑 네비게이션 종료 중...")
            sensorViewModel.stopNavigation()
            sendAverageHeartRateToMobile()
        }
        finish()
    }

    private fun sendAverageHeartRateToMobile() {
        val averageHeartRate = sensorViewModel.getAverageHeartRateDuringNavigation()
        Log.d("NavigationActivity-Watch", "📡 평균 심박수 전송 준비: $averageHeartRate BPM")

        val dataMap = PutDataMapRequest.create("/navigation/average_heartbeat").apply {
            dataMap.putFloat("averageHeartRate", averageHeartRate)
        }

        Wearable.getDataClient(this).putDataItem(dataMap.asPutDataRequest()).addOnSuccessListener {
            Log.d("NavigationActivity-Watch", "✅ 평균 심박수 전송 성공: $averageHeartRate BPM")
        }.addOnFailureListener { e ->
            Log.e("NavigationActivity-Watch", "🚨 평균 심박수 전송 실패", e)
        }
    }


    private fun checkAndRequestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.BODY_SENSORS,
            Manifest.permission.ACTIVITY_RECOGNITION
        )

        val neededPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (neededPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, neededPermissions.toTypedArray(), 1001)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Log.d("NavigationActivity-Watch", "✅ 모든 권한이 승인됨!")
            } else {
                Log.e("NavigationActivity-Watch", "🚨 권한이 거부됨! 센서 데이터를 읽을 수 없음.")
            }
        }
    }


}
