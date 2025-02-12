package com.example.drawrun.presentation

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.drawrun.presentation.sensors.SensorManagerHelper
import com.example.drawrun.presentation.sensors.SensorViewModel
import com.example.drawrun.presentation.ui.NavigationScreen
import com.example.drawrun.presentation.sensors.SensorViewModelFactory
class NavigationActivity : ComponentActivity() {

    private val dataViewModel: DataViewModel by viewModels()
    private lateinit var sensorManagerHelper: SensorManagerHelper
    private val sensorViewModel: SensorViewModel by viewModels {
        SensorViewModelFactory(sensorManagerHelper)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 서비스 종료 이벤트 리시버 등록
        val filter = IntentFilter("com.example.drawrun.SERVICE_STOPPED")
        registerReceiver(serviceStoppedReceiver, filter, Context.RECEIVER_NOT_EXPORTED)

        sensorManagerHelper = SensorManagerHelper(this)

        // 심박수 추적 시작
        startHeartRateMeasurement()

        Log.d("NavigationActivity", "registerReceiver 호출 직전")
//        registerReceiver(
//            navigationUpdateReceiver,
//            IntentFilter("com.example.drawrun.presentation.NAVIGATION_UPDATE"),
//            Context.RECEIVER_EXPORTED
//        )
        LocalBroadcastManager.getInstance(this).registerReceiver(
            navigationUpdateReceiver,
            IntentFilter("com.example.drawrun.presentation.NAVIGATION_UPDATE")
        )
        Log.d("NavigationActivity", "registerReceiver 호출 완료")
        setContent {
            NavigationScreen(dataViewModel, sensorViewModel){
                val intent = Intent(this, DrawRunMainActivity::class.java)
                startActivity(intent)
                finish()  // 현재 액티비티 종료
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("NavigationActivity", "onResume 호출됨 - Activity 활성 상태")
        dataViewModel.forceRefresh()
    }

    private val navigationUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            Log.d("NavigationReceiver", "onReceive 호출됨 - 인텐트 액션: $action")
            val distanceToNextTurn = intent.getDoubleExtra("distanceToNextTurn", 0.0)
            val voiceInstruction = intent.getStringExtra("voiceInstruction") ?: "안내 없음"
            val totalDistance = intent.getDoubleExtra("totalDistance", 0.0)
            val distanceRemaining = intent.getDoubleExtra("distanceRemaining", 0.0)

            Log.d(
                "NavigationReceiver",
                "onReceive 호출됨 - distanceToNextTurn=$distanceToNextTurn, voiceInstruction=$voiceInstruction, " +
                        "totalDistance=$totalDistance, distanceRemaining=$distanceRemaining"
            )

            dataViewModel.updateData(distanceToNextTurn, voiceInstruction, totalDistance, distanceRemaining)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(navigationUpdateReceiver)
        // 리시버 해제
        unregisterReceiver(serviceStoppedReceiver)
//        sendAverageHeartRateToMobile()
        sensorManagerHelper.startSensors()
        Log.d("NavigationActivity", "Navigation 종료 - 메인 화면이동")
//        val intent = Intent(this, DrawRunMainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//        startActivity(intent)
    }

    private fun startHeartRateMeasurement() {
        sensorManagerHelper.startSensors()  // 센서 측정 시작
        Log.d("NavigationActivity", "심박수 측정 시작")
    }

//    private fun sendAverageHeartRateToMobile() {
//        val averageHeartRate = sensorViewModel.getAverageHeartRate()
//        Log.d("NavigationActivity", "평균 심박수 전송 준비: $averageHeartRate BPM")
//
//        val dataMap = PutDataMapRequest.create("/navigation/average_heartbeat").apply {
//            dataMap.putFloat("averageHeartRate", averageHeartRate)
//        }
//        Wearable.getDataClient(this).putDataItem(dataMap.asPutDataRequest()).addOnSuccessListener {
//            Log.d("NavigationActivity", "평균 심박수 전송 성공")
//        }.addOnFailureListener { e ->
//            Log.e("NavigationActivity", "평균 심박수 전송 실패", e)
//        }
//    }

    private val serviceStoppedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
//            if (intent.action == "com.example.drawrun.SERVICE_STOPPED") {
//                Log.d("NavigationActivity", "서비스 중단 감지 - 메인 화면으로 이동")
//                navigateToMainScreen()
//            }
        }
    }



}
