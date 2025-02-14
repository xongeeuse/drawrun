package com.example.drawrun.services

import android.util.Log
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService

class DataReceiverService : WearableListenerService() {

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)

        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val dataItem = event.dataItem
                if (dataItem.uri.path == "/navigation/average_heartbeat") {
                    val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
                    val averageHeartRate = dataMap.getFloat("averageHeartRate")

                    Log.d("DataReceiverService", "📡 모바일에서 평균 심박수 수신: $averageHeartRate BPM")

                    // TODO: 여기서 평균 심박수를 ViewModel 또는 DB에 저장하는 로직 추가 가능
                }
            }
        }
    }
}
