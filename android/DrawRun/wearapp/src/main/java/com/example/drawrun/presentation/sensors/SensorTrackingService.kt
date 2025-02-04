package com.example.drawrun.presentation.sensors

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.drawrun.R

class SensorTrackingService : Service() {

    companion object {
        private const val CHANNEL_ID = "sensor_tracking_channel"
        private const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("SensorTrackingService", "Service created")

        // Foreground 알림 채널 생성 및 startForeground 호출
        createNotificationChannel()
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)

        Log.d("SensorTrackingService", "Foreground service started in onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("SensorTrackingService", "Service started with START_STICKY")

        // 필요한 센서 동작 시작 또는 다른 작업 수행
        startSensorTracking()

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("SensorTrackingService", "Service stopped")
        stopSensorTracking()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Sensor Tracking Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (manager.getNotificationChannel(CHANNEL_ID) == null) {
                manager.createNotificationChannel(channel)
            }
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("DrawRun 실행 중")
            .setContentText("GPS 및 센서 데이터를 수집하고 있습니다.")
            .setSmallIcon(R.mipmap.draw_run_logo)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    private fun startSensorTracking() {
        // 센서 로직 시작
        Log.d("SensorTrackingService", "Sensors started")
    }

    private fun stopSensorTracking() {
        // 센서 로직 종료
        Log.d("SensorTrackingService", "Sensors stopped")
    }
}
