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

class RunRecordActivity : ComponentActivity() {
    private var totalDistance: Double = 0.0
    private var totalDuration: Int = 0
    private var averageHeartRate: Float = 0f
    private var pathId: Int = 0
    private var trackingSnapshotUrl: String? = null
//    private lateinit var viewModel: RunRecordViewModel  // ✅ ViewModel 제거
    private lateinit var heartRateTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_runrecord)

        Log.d("RunRecordActivity", "🟢 onCreate 시작됨 - RunRecordActivity")

        val trackingImageView = findViewById<ImageView>(R.id.trackingImageView)
        val distanceTextView = findViewById<TextView>(R.id.textDistance)
        val timeTextView = findViewById<TextView>(R.id.textTime)
        val heartRateTextView = findViewById<TextView>(R.id.textHeartRate)
        val finishButton = findViewById<Button>(R.id.buttonFinish)

        // ✅ Intent 데이터 받기
        trackingSnapshotUrl = intent.getStringExtra("trackingSnapshotUrl")
        pathId = intent.getIntExtra("pathId", 1)  // pathId가 없으면 기본값 1

        Log.d("RunRecordActivity", "🟢 받은 pathId: $pathId")
        Log.d("RunRecordActivity", "🟢 받은 trackingSnapshotUrl: $trackingSnapshotUrl")

        if (trackingImageView == null) {
            Log.e("RunRecordActivity", "🚨 ERROR: trackingImageView가 UI에서 존재하지 않음!")
        } else {
            Log.d("RunRecordActivity", "✅ trackingImageView 찾음! 이미지 로드 시도")
        }

        if (!isDestroyed && !isFinishing) {
            trackingSnapshotUrl?.let {
                Glide.with(this)
                    .load(it)
                    .placeholder(R.drawable.search_background)  // 기본 이미지 설정
                    .into(trackingImageView)
                Log.d("RunRecordActivity", "🟢 Glide로 이미지 로드 성공")
            } ?: Log.e("RunRecordActivity", "❌ trackingSnapshotUrl이 null이거나 비어 있음")
        } else {
            Log.e("RunRecordActivity", "🚨 ERROR: Activity가 종료된 상태에서 Glide 실행 시도!")
        }

        // ✅ 거리, 시간, 심박수 데이터 받기
        totalDistance = intent.getDoubleExtra("totalDistance", 0.0) // 총 이동 거리 (미터)
        val distanceInKm = intent.getDoubleExtra("distanceInKm", 0.0) // 킬로미터 변환된 거리
        totalDuration = intent.getIntExtra("totalDuration", 0) // 총 소요 시간 (초)
        val time = intent.getIntExtra("time", 0) // 변환된 소요 시간 (분/초 형식)
        averageHeartRate = intent.getFloatExtra("averageHeartRate", 0f)

        // ✅ 데이터 로그 확인
        Log.d("RunRecordActivity", "🏃‍♂️ 받은 데이터 - 거리: ${distanceInKm}km, 시간: ${time}s, 평균 심박수: ${averageHeartRate} BPM")

        // ✅ UI 요소에 값 설정
        distanceTextView.text = String.format("%.2f km", distanceInKm)
        timeTextView.text = formatTime(totalDuration) // 변환된 시간 포맷
        heartRateTextView.text = String.format("평균 심박수: %.0f BPM", averageHeartRate)

        Log.d("RunRecordActivity", "🟢 UI 업데이트 완료 - 거리: ${distanceInKm}km, 시간: ${formatTime(totalDuration)}, 심박수: ${averageHeartRate} BPM")

        // ✅ '러닝 기록 저장' 버튼 클릭 시 처리 (현재 API 요청은 실행하지 않음)
        finishButton.setOnClickListener {
            Log.d("RunRecordActivity", "🚀 러닝 기록 저장 버튼 클릭됨 (현재 API 요청은 실행 안됨)")
        }

        // ✅ ViewModel 관련 코드 주석 처리
//        val factory = RunRecordViewModelFactory(this)
//        viewModel = ViewModelProvider(this)[RunRecordViewModel::class.java]

//        lifecycleScope.launch {
//            viewModel.saveResult.collect { result ->
//                result?.onSuccess {
//                    Toast.makeText(this@RunRecordActivity, "저장 성공!", Toast.LENGTH_SHORT).show()
//                }?.onFailure {
//                    Toast.makeText(this@RunRecordActivity, "저장 실패: ${it.message}", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
    }

    // ✅ 초 → "X분 Y초" 형식 변환 함수
    private fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return "${minutes}분 ${remainingSeconds}초"
    }

    // ✅ 저장 후 메인 화면으로 이동하는 함수
    private fun navigateToMainScreen() {
        val intent = Intent(this, MainActivity::class.java) // MainActivity로 이동
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish() // 현재 액티비티 종료
    }

    // 📡 BroadcastReceiver 설정 (심박수 데이터 받기)
    private val heartRateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val heartRate = intent?.getFloatExtra("averageHeartRate", 0f) ?: 0f
            Log.d("RunRecordActivity", "💓 수신된 평균 심박수: $heartRate BPM")

            // UI 업데이트
            heartRateTextView.text = "평균 심박수: %.0f BPM".format(heartRate)
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onResume() {
        super.onResume()
        registerReceiver(heartRateReceiver, IntentFilter("com.example.drawrun.HEART_RATE_UPDATE"))
        Log.d("RunRecordActivity", "📡 브로드캐스트 리시버 등록 완료")
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(heartRateReceiver)
        Log.d("RunRecordActivity", "📡 브로드캐스트 리시버 해제 완료")
    }
}
