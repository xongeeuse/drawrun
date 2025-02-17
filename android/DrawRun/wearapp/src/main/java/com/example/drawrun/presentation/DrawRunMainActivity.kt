package com.example.drawrun.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModelProvider
import com.example.drawrun.presentation.theme.DrawRunTheme
import com.example.drawrun.presentation.ui.DrawRunMainScreen
import com.example.drawrun.presentation.ui.SplashScreen
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drawrun.presentation.sensors.SensorManagerHelper
import com.example.drawrun.presentation.sensors.SensorViewModel
import com.example.drawrun.presentation.sensors.SensorViewModelFactory

class DrawRunMainActivity : ComponentActivity() {

    private lateinit var sensorManagerHelper: SensorManagerHelper
    private lateinit var sensorViewModel: SensorViewModel


    private val messageListener = { messageEvent: MessageEvent ->
        if (messageEvent.path == "/launch_app") {
            startActivity(Intent(this, DrawRunMainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // SensorManagerHelper 초기화
        sensorManagerHelper = SensorManagerHelper(this)

        // ✅ `ViewModelProvider`를 사용하여 `sensorViewModel`을 안전하게 초기화
        sensorViewModel = ViewModelProvider(
            this, SensorViewModelFactory(sensorManagerHelper)
        )[SensorViewModel::class.java]

        // 메시지 수신 등록
        Wearable.getMessageClient(this).addListener(messageListener)

        setContent {
            // ViewModel 초기화
            val viewModel: SensorViewModel = viewModel(
                factory = SensorViewModelFactory(sensorManagerHelper)
            )
            val currentHeartRate by viewModel.heartRate.collectAsState()
            // 센서 측정 시작
            LaunchedEffect(Unit) {
                viewModel.startMeasurement()
                Log.d("DrawRunMainActivity", "Measurement started")
                Log.d("DrawRunMainActivity", "🔥 UI 업데이트: 현재 심박수 = ${currentHeartRate ?: "N/A"} BPM")
            }

            // 상태값을 사용하여 화면 전환 구현
            var showSplash by remember { mutableStateOf(true) }

            DrawRunTheme {
                if (showSplash) {
                    SplashScreen(onFinish = {
                        showSplash = false
                    })
                } else {
                    DrawRunMainScreen(viewModel = sensorViewModel, context = this@DrawRunMainActivity)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Wearable.getMessageClient(this).removeListener(messageListener)
    }
}
