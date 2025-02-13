package com.example.drawrun.ui.runrecord

import android.content.Intent
import android.util.Log
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService

class DataReceiverService : WearableListenerService() {

    override fun onDataChanged(dataEventBuffer: DataEventBuffer) {
        Log.d("DataReceiverService", "📡 데이터 변경 이벤트 수신")

        for (event in dataEventBuffer) {
            if (event.type == DataEvent.TYPE_CHANGED && event.dataItem.uri.path == "/navigation/average_heartbeat") {
                val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                val averageHeartRate = dataMap.getFloat("averageHeartRate", 0f)

                Log.d("DataReceiverService", "✅ 워치에서 평균 심박수 수신: $averageHeartRate BPM")

                // 📌 RunRecordActivity로 전달하기 위해 Intent 사용
                val intent = Intent(this, RunRecordActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra("averageHeartRate", averageHeartRate)
                }
                startActivity(intent)
            }
        }
    }
}
