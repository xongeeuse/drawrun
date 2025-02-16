package com.example.drawrun.presentation

import android.content.Intent
import android.util.Log
import com.example.drawrun.presentation.sensors.SensorManagerHelper
import com.example.drawrun.presentation.sensors.SensorViewModel
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class DataReceiverService : WearableListenerService() {
    private lateinit var sensorViewModel: SensorViewModel
    override fun onCreate() {
        super.onCreate()
        Log.d("receiverService-WatchData", "DataReceiverService 시작됨")
        sensorViewModel = SensorViewModel(SensorManagerHelper(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("receiverService-WatchData", "DataReceiverService 종료됨")

        val intent = Intent("com.example.drawrun.SERVICE_STOPPED")
        sendBroadcast(intent)
        Log.d("DataReceiverService", "서비스가 종료됨")
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        Log.d("receiverService-WatchData", "메시지 수신 경로: ${messageEvent.path}, 데이터: ${String(messageEvent.data)}")
        if (messageEvent.path == "/start_navigation") {
            Log.d("receiverService-WatchData", "내비게이션 시작 명령 수신")

            val intent = Intent(this, NavigationActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } else {
            Log.d("receiverService-WatchData", "알 수 없는 메시지 경로: ${messageEvent.path}")
        }
    }

    override fun onDataChanged(dataEventBuffer: DataEventBuffer) {
        Log.d("receiverService-WatchData", "데이터 변경 이벤트 수신")
        for (event in dataEventBuffer) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val path = event.dataItem.uri.path
                val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap

                // ✅ 1. 워치에서 내비 상태 데이터 (`isNavigationRunning`) 수신 처리
                if (path == "/navigation/status") {
                    val isNavigationRunning = dataMap.getBoolean("isNavigationRunning", false)
                    Log.d("receiverService-WatchData", "📡 워치에서 네비 상태 수신: $isNavigationRunning")

                    // ✅ SensorViewModel 업데이트
                    sensorViewModel.updateNavigationStateFromWatch(isNavigationRunning)
                }

                // ✅ 2. 내비게이션 안내 데이터 (`instructions`) 수신 처리
                if (path == "/navigation/instructions") {
                    val distanceToNextTurn = dataMap.getDouble("distanceToNextTurn")
                    val voiceInstruction = dataMap.getString("voiceInstruction") ?: "안내 없음"
                    val totalDistance = dataMap.getDouble("totalDistance")
                    val distanceRemaining = dataMap.getFloat("distanceRemaining").toDouble()

                    Log.d("receiverService-WatchData", "📍 수신 데이터 - 다음 회전까지: ${distanceToNextTurn}m, 안내: $voiceInstruction")

                    // 📡 데이터를 `NavigationActivity`로 브로드캐스트 전송
                    sendNavigationUpdate(distanceToNextTurn, voiceInstruction, totalDistance, distanceRemaining)
                }
            } else {
                Log.d("receiverService-WatchData", "❌ 알 수 없는 데이터 경로: ${event.dataItem.uri.path}")
            }
        }
    }


    private fun sendNavigationUpdate(
        distanceToNextTurn: Double,
        voiceInstruction: String,
        totalDistance: Double,
        distanceRemaining: Double
    ) {
        val intent = Intent("com.example.drawrun.presentation.NAVIGATION_UPDATE").apply {
            putExtra("distanceToNextTurn", distanceToNextTurn)
            putExtra("voiceInstruction", voiceInstruction)
            putExtra("totalDistance", totalDistance)
            putExtra("distanceRemaining", distanceRemaining)
        }
        sendBroadcast(intent)
        Log.d("DataReceiverService", "📡 브로드캐스트 전송 완료")
    }
}
