package com.example.drawrun.presentation

import android.content.Intent
import android.util.Log
import com.google.android.gms.wearable.*

class NavigationDataListener : WearableListenerService(), DataClient.OnDataChangedListener {

    override fun onCreate() {
        super.onCreate()
        Log.d("NavigationDataListener", "NavigationDataListener 서비스 시작됨")
    }


    override fun onDataChanged(dataEventBuffer: DataEventBuffer) {
        Log.d("WatchData", "데이터 변경 이벤트 발생")
        dataEventBuffer.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED && event.dataItem.uri.path == "/navigation/progress") {
                Log.d("NavigationDataListener", "데이터 이벤트 수신: 경로 = ${event.dataItem.uri.path}")
                val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                val totalDistance = dataMap.getDouble("totalDistance")
                val distanceRemaining = dataMap.getDouble("distanceRemaining")
                val direction = dataMap.getString("direction")
                val progress = dataMap.getDouble("progress")

                // 로그 추가
                Log.d("WatchData", "총 거리: $totalDistance, 남은 거리: $distanceRemaining, 방향: $direction, 진행률: $progress%")

                // 데이터를 UI에 반영하는 함수 호출 (UI 업데이트)
                if (direction != null) {
                    updateWatchUI(progress, direction, totalDistance, distanceRemaining)
                }
            }
        }
    }

    private fun updateWatchUI(progress: Double, direction: String, totalDistance: Double, distanceRemaining: Double) {
        Log.d("WatchUI", "UI 업데이트: 진행률 ${progress}%, 방향 안내: $direction")
        // 여기에서 Jetpack Compose 또는 Canvas로 프로그레스바 UI 업데이트
    }
}