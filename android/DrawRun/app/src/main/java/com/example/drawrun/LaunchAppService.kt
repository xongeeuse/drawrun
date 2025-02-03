package com.example.drawrun

import android.content.Intent
import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class LaunchAppService : WearableListenerService() {

    override fun onCreate() {
        super.onCreate()
        Log.d("DrawRun", "LaunchAppService 시작됨")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("DrawRun", "LaunchAppService 실행 중")
        return START_STICKY
    }


    override fun onMessageReceived(messageEvent: MessageEvent) {
        val receivedData = messageEvent.data?.toString(Charsets.UTF_8) ?: "No data"
        Log.d("DrawRun", "수신된 메시지 데이터: $receivedData")
        Log.d("DrawRun", "워치에서 메시지 수신 성공: ${messageEvent.path}")

        if (messageEvent.path == "/launch_app") {
            Log.d("DrawRun", "메시지 경로 일치: MainActivity 실행 시도")

            val activityIntent = Intent(this, SettingsActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            startActivity(activityIntent)
        } else {
            Log.d("DrawRun", "메시지 경로가 일치하지 않음: ${messageEvent.path}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("DrawRun", "LaunchAppService 종료됨")
    }
}
