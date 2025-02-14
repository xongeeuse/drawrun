package com.example.drawrun.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.android.gms.wearable.*

class MobileDataReceiverService : Service(), DataClient.OnDataChangedListener {

    override fun onCreate() {
        super.onCreate()
        Wearable.getDataClient(this).addListener(this)  // 📌 데이터 리스너 등록
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val dataItem = event.dataItem

                if (dataItem.uri.path == "/navigation/average_heartbeat") { // ✅ 워치에서 보낸 데이터 체크
                    val dataMap = DataMapItem.fromDataItem(dataItem).dataMap  // 📌 getDataMapItem 대신 사용
                    val averageHeartRate = dataMap.getFloat("averageHeartRate", 0.0f)

                    Log.d("MobileDataReceiverService", "📡 평균 심박수 수신: $averageHeartRate BPM")

                    // ✅ 📡 `RunRecordActivity`로 데이터 전달
                    val intent = Intent("com.example.drawrun.HEART_RATE_UPDATE").apply {
                        putExtra("averageHeartRate", averageHeartRate)  // 🔹 putExtra 문제 해결
                    }
                    sendBroadcast(intent)  // 📡 Broadcast로 데이터 전송
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Wearable.getDataClient(this).removeListener(this)  // 📌 리스너 해제
    }
}
