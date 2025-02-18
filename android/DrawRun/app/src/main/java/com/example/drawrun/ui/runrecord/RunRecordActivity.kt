package com.example.drawrun.ui.runrecord

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.drawrun.MainActivity
import com.example.drawrun.R
import com.example.drawrun.data.dto.request.runrecord.RunRecordRequest
import com.example.drawrun.data.repository.MasterpieceRepository
import com.example.drawrun.utils.RetrofitInstance
import com.example.drawrun.viewmodel.MasterpieceViewModel
import com.example.drawrun.viewmodel.MasterpieceViewModelFactory
import com.example.drawrun.viewmodel.RunRecordViewModel
import com.example.drawrun.viewmodel.RunRecordViewModelFactory
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.launch
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

    private val viewModel: RunRecordViewModel by viewModels {
        RunRecordViewModelFactory(RetrofitInstance.RunRecordApi(this))
    }

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
        averageHeartRate = intent.getFloatExtra("averageHeartRate", 1F)

        Log.d("RunRecordActivity", "🟢 받은 pathId: $pathId")
        Log.d("RunRecordActivity", "🟢 받은 trackingSnapshotUrl: $trackingSnapshotUrl")

        // ✅ UI 업데이트
        updateUI(distanceInKm, totalDuration, averageHeartRate)

        // NaviActivity에서 전달된 데이터 받기
        val isMasterpieceRequest = intent.getBooleanExtra("isMasterpieceRequest", false)
        val masterpieceSegId = intent.getIntExtra("masterpieceSegId", -1)

        // Masterpiece 요청일 경우 complete API 호출
        if (isMasterpieceRequest && masterpieceSegId != -1) {
            val repository = MasterpieceRepository(RetrofitInstance.MasterpieceApi(this))
            val masterpieceViewModelFactory = MasterpieceViewModelFactory(repository)
            val masterpieceViewModel: MasterpieceViewModel = ViewModelProvider(this, masterpieceViewModelFactory)[MasterpieceViewModel::class.java]

            masterpieceViewModel.completeMasterpiece(masterpieceSegId)
            masterpieceViewModel.completeMasterpieceResult.observe(this) { isSuccess ->
                if (isSuccess) {
                    Log.d("RunRecordActivity", "Masterpiece 완료 요청 성공")
                    Toast.makeText(this, "마스터피스 완료 성공", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("RunRecordActivity", "Masterpiece 완료 요청 실패")
                    Toast.makeText(this, "마스터피스 완료 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // ✅ '러닝 기록 저장' 버튼 클릭 시 처리
        finishButton.setOnClickListener {
            if (averageHeartRate == -1f) {
                Log.e("RunRecordActivity", "🚨 심박수 데이터가 아직 업데이트되지 않음! 저장 중단")
                Toast.makeText(this, "심박수 데이터를 가져오는 중입니다. 잠시만 기다려주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            saveRunRecord()
            Log.d("RunRecordActivity", "🚀 러닝 기록 저장 버튼 클릭됨")
        }
        // ✅ 서버 저장 결과 감지하여 UI 업데이트
        lifecycleScope.launch {
            viewModel.saveResult.collect { result ->
                result?.let {
                    if (it.isSuccess) {
                        Log.d("RunRecordActivity", "✅ 러닝 기록 저장 성공")
                        navigateToMainScreen()
                    } else {
                        Log.e("RunRecordActivity", "🚨 러닝 기록 저장 실패: ${it.exceptionOrNull()?.message}")
                    }
                }
            }
        }
    }

    // ✅ UI 업데이트 함수
    private fun updateUI(distance: Double, timeSeconds: Int, heartRate: Float) {
        dateTextView.text = SimpleDateFormat("yyyy.MM.dd (E) 러닝 기록", Locale.KOREA).format(Date())
        val paceSeconds = calculatePaceInSeconds(distance, timeSeconds)
        distanceTextView.text = String.format("%.2f km", distance)
        timeTextView.text = formatTime(timeSeconds)
        paceTextView.text = formatPaceToString(paceSeconds)
        heartRateTextView.text = String.format("%d BPM", heartRate.toInt())

        // ✅ 트래킹 스냅샷 이미지 로드 (Glide) - `null` 체크 후 실행
        trackingSnapshotUrl?.let {
            Glide.with(this)
                .load(it)
                .placeholder(R.drawable.search_background)  // 기본 이미지
                .into(trackingImageView)
        }
    }
    // ✅ 초 단위를 "X'YY\"" 형식으로 변환하는 함수
    private fun formatPaceToString(paceSeconds: Int): String {
        val minutes = paceSeconds / 60
        val seconds = paceSeconds % 60
        val formattedPace = String.format("%d'%02d\"", minutes, seconds)

        Log.d("RunRecordActivity", "✅ UI 변환 pace: ${paceSeconds}초 -> $formattedPace") // 🔥 변환 로그 추가
        return formattedPace
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

    private fun calculatePaceInSeconds(distanceKm: Double, runningTimeSeconds: Int): Int {
        return when {
            distanceKm == 0.0 -> 0  // 예외처리
            distanceKm < 0.5 -> (runningTimeSeconds / (distanceKm * 1000)).toInt().coerceAtLeast(5) // 500m 이하는 미터 단위 pace 보정
            else -> (runningTimeSeconds / distanceKm).toInt().coerceAtLeast(10)  // 정상적인 거리에서는 기존 km 단위 pace 적용
        }
    }

    // ✅ 저장 후 메인 화면으로 이동하는 함수
    private fun navigateToMainScreen() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }


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

                        if (receivedHeartRate > 0) {
                            averageHeartRate = receivedHeartRate
                            Log.d("RunRecordActivity", "✅ 심박수 업데이트 완료: $averageHeartRate BPM")

                            // ✅ 버튼 활성화
                            runOnUiThread {
                                finishButton.isEnabled = true
                            }
                        } else {
                            Log.e("RunRecordActivity", "🚨 심박수 값이 올바르지 않음")
                        }

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

    private fun saveRunRecord() {
        if (averageHeartRate == -1f) {
            Log.e("RunRecordActivity", "🚨 심박수 데이터가 아직 업데이트되지 않음! 저장 중단")
            return
        }
        val runImgUrl = trackingSnapshotUrl
        val distanceKm = totalDistance
        val timeS = totalDuration
        val paceS = calculatePaceInSeconds(totalDistance, totalDuration) // ✅ 초 단위 변환
        val heartbeat = averageHeartRate.toInt()
        Log.d("RunRecordActivity", "✅ 거리: $distanceKm km, 시간: $timeS 초")
        Log.d("RunRecordActivity", "✅ paceS 계산값: $paceS 초") // 🔥 디버깅 추가

        val request = RunRecordRequest(
            runImgUrl = runImgUrl,
            distanceKm = distanceKm,
            timeS = timeS,
            paceS = paceS, // ✅ 초 단위 저장
            state = 1,
            heartbeat = heartbeat,
            cadence = 1
        )

        Log.d("RunRecordActivity", "📡 러닝 기록 저장 요청 데이터: $request")
        viewModel.saveRunRecord(request)
    }


}
