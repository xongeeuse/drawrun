package com.example.drawrun.presentation

import android.content.Intent
import android.util.Log
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class DataReceiverService : WearableListenerService() {

    override fun onCreate() {
        super.onCreate()
        Log.d("receiverService-WatchData", "DataReceiverService 시작됨")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("receiverService-WatchData", "DataReceiverService 종료됨")
        // 서비스가 중단되었을 때 브로드캐스트 전송
        val intent = Intent("com.example.drawrun.SERVICE_STOPPED")
        sendBroadcast(intent)
        Log.d("DataReceiverService", "서비스가 종료됨")
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        Log.d("receiverService-WatchData", "메시지 수신 경로: ${messageEvent.path}, 데이터: ${String(messageEvent.data)}")
        if (messageEvent.path == "/start_navigation") {
            Log.d("receiverService-WatchData", "내비게이션 시작 명령 수신")

            val intent = Intent(this, RunningActivity::class.java).apply {
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
            if (event.type == DataEvent.TYPE_CHANGED && event.dataItem.uri.path == "/navigation/instructions") {
                val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                val distanceToNextTurn = dataMap.getDouble("distanceToNextTurn")
                val voiceInstruction = dataMap.getString("voiceInstruction") ?: "안내 없음"
                val totalDistance = dataMap.getDouble("totalDistance")
                val distanceRemaining = dataMap.getFloat("distanceRemaining")
                Log.d("receiverService-WatchData", "수신 경로: ${event.dataItem.uri.path}")
                Log.d(
                    "receiverService-WatchData",
                    "수신 데이터 - 다음 회전까지: ${distanceToNextTurn}m, 안내: $voiceInstruction, " +
                            "총 거리: $totalDistance, 남은 거리: $distanceRemaining"
                )

                // 데이터를 Broadcast로 전달
                sendDataToViewModel(distanceToNextTurn, voiceInstruction, totalDistance, distanceRemaining)
            } else {
                Log.d("receiverService-WatchData", "알 수 없는 데이터 경로: ${event.dataItem.uri.path}")
            }
        }
    }

    private fun sendDataToViewModel(
        distanceToNextTurn: Double,
        voiceInstruction: String,
        totalDistance: Double,
        distanceRemaining: Float
    ) {
        Log.d(
            "DataReceiverService",
            "sendDataToViewModel 호출됨 - distanceToNextTurn=$distanceToNextTurn, voiceInstruction=$voiceInstruction, " +
                    "totalDistance=$totalDistance, distanceRemaining=$distanceRemaining"
        )
        val intent = Intent("com.example.drawrun.presentation.NAVIGATION_UPDATE").apply {
            putExtra("distanceToNextTurn", distanceToNextTurn)
            putExtra("voiceInstruction", voiceInstruction)
            putExtra("totalDistance", totalDistance)
            putExtra("distanceRemaining", distanceRemaining.toDouble())
        }
        Log.d("DataReceiverService", "브로드캐스트 송신: $intent")
        sendBroadcast(intent)
        Log.d("DataReceiverService", "브로드캐스트 전송 완료")
    }
}
