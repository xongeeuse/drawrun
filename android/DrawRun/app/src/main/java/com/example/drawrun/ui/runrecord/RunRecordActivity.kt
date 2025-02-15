package com.example.drawrun.ui.runrecord

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.bumptech.glide.Glide
import com.example.drawrun.MainActivity
import com.example.drawrun.R
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RunRecordActivity : ComponentActivity() {
    private var totalDistance: Double = 0.0
    private var totalDuration: Int = 0
    private var averageHeartRate: Float = 0f
    private var pathId: Int = 0
    private var trackingSnapshotUrl: String? = null

    // ✅ UI 요소 캐싱 (findViewById 호출 최소화)
    private lateinit var trackingImageView: ImageView
    private lateinit var distanceTextView: TextView
    private lateinit var timeTextView: TextView
    private lateinit var heartRateTextView: TextView
    private lateinit var finishButton: Button
    private lateinit var dateTextView: TextView
    private lateinit var paceTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_runrecord)

        Log.d("RunRecordActivity", "🟢 onCreate 시작됨 - RunRecordActivity")

        // ✅ UI 요소 연결 (한 번만 findViewById 호출)
        trackingImageView = findViewById(R.id.trackingImageView)
        distanceTextView = findViewById(R.id.textDistance)
        timeTextView = findViewById(R.id.textRunningTime)
        heartRateTextView = findViewById(R.id.textHeartRate)
        finishButton = findViewById(R.id.buttonFinish)
        dateTextView = findViewById(R.id.textDate)
        paceTextView = findViewById(R.id.textAvgPace)

        // ✅ Intent 데이터 받기
        trackingSnapshotUrl = intent.getStringExtra("trackingSnapshotUrl")
        pathId = intent.getIntExtra("pathId", 1)

        // ✅ 거리, 시간, 심박수 데이터 받기
        totalDistance = intent.getDoubleExtra("totalDistance", 0.0)
        val distanceInKm = intent.getDoubleExtra("distanceInKm", 0.0)
        totalDuration = intent.getIntExtra("totalDuration", 0)
        averageHeartRate = intent.getFloatExtra("averageHeartRate", -1f)

        Log.d("RunRecordActivity", "🟢 받은 평균 심박수: $averageHeartRate BPM")

        Log.d("RunRecordActivity", "🟢 받은 pathId: $pathId")
        Log.d("RunRecordActivity", "🟢 받은 trackingSnapshotUrl: $trackingSnapshotUrl")

        // ✅ UI 업데이트
        updateUI(distanceInKm, totalDuration, averageHeartRate)

        // ✅ '러닝 기록 저장' 버튼 클릭 시 처리
        finishButton.setOnClickListener {
            Log.d("RunRecordActivity", "🚀 러닝 기록 저장 버튼 클릭됨 (현재 API 요청은 실행 안됨)")
        }
    }

    // ✅ UI 업데이트 함수
    private fun updateUI(distance: Double, timeSeconds: Int, heartRate: Float) {
        dateTextView.text = SimpleDateFormat("yyyy.MM.dd (E) 러닝 기록", Locale.KOREA).format(Date())

        distanceTextView.text = String.format("%.2f km", distance)
        timeTextView.text = formatTime(timeSeconds)
        paceTextView.text = calculatePace(distance, timeSeconds)
        heartRateTextView.text = String.format("%d BPM", heartRate.toInt())

        // ✅ 트래킹 스냅샷 이미지 로드 (Glide) - `null` 체크 후 실행
        trackingSnapshotUrl?.let {
            Glide.with(this)
                .load(it)
                .placeholder(R.drawable.search_background)  // 기본 이미지
                .into(trackingImageView)
        }
    }

    // ✅ 초 → "X분 Y초" 형식 변환 함수
    private fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    // ✅ 평균 페이스 계산 (거리 0일 때 예외처리 추가)
    private fun calculatePace(distanceKm: Double, runningTimeSeconds: Int): String {
        return if (distanceKm == 0.0) "0'00\"" else {
            val pacePerKm = runningTimeSeconds / distanceKm
            val minutes = (pacePerKm / 60).toInt()
            val seconds = (pacePerKm % 60).toInt()
            String.format("%d'%02d\"", minutes, seconds)
        }
    }

    // ✅ 저장 후 메인 화면으로 이동하는 함수
    private fun navigateToMainScreen() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

//    // 📡 BroadcastReceiver 설정 (심박수 데이터 받기)
//    private val heartRateReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            val heartRate = intent?.getFloatExtra("averageHeartRate", 0f) ?: 0f
//            Log.d("RunRecordActivity", "💓 수신된 평균 심박수: $heartRate BPM")
//
//            if (heartRate > 0f) {
//                runOnUiThread {
//                    heartRateTextView.text = String.format("%d BPM", heartRate.toInt())
//                }
//            } else {
//                Log.e("RunRecordActivity", "🚨 수신된 심박수 값이 올바르지 않음")
//            }
//        }
//    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onResume() {
        super.onResume()

        getAverageHeartRateFromWearable()
    }

    override fun onPause() {
        super.onPause()
    }

    private fun getAverageHeartRateFromWearable() {
        Wearable.getDataClient(this).dataItems
            .addOnSuccessListener { dataItemBuffer ->
                for (dataItem in dataItemBuffer) {
                    if (dataItem.uri.path == "/navigation/average_heartbeat") {
                        val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
                        val receivedHeartRate = dataMap.getFloat("averageHeartRate", -1f)
                        Log.d("RunRecordActivity", "💓 Wearable에서 받은 평균 심박수: $receivedHeartRate BPM")

                        // UI 업데이트
                        runOnUiThread {
                            heartRateTextView.text = String.format("%d BPM", receivedHeartRate.toInt())
                        }
                    }
                }
                dataItemBuffer.release()
            }
            .addOnFailureListener { e ->
                Log.e("RunRecordActivity", "🚨 Wearable에서 평균 심박수 가져오기 실패", e)
            }
    }


}
