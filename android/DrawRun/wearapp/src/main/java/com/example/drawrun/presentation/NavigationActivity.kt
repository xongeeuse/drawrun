package com.example.drawrun.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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

        sensorManagerHelper = SensorManagerHelper(this)

        registerReceiver(
            navigationUpdateReceiver,
            IntentFilter("com.example.drawrun.presentation.NAVIGATION_UPDATE"),
            Context.RECEIVER_EXPORTED
        )

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
                dataViewModel.updateData(distanceToNextTurn, voiceInstruction, totalDistance, distanceRemaining)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(navigationUpdateReceiver)
        sensorManagerHelper.startSensors()
    }


    private fun sendAverageHeartRateToMobile() {
        val averageHeartRate =
            sensorViewModel.getAverageHeartRateDuringNavigation() // 📌 평균 심박수 가져오기
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


}
