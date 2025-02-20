package com.example.drawrun.ui.mypage

import android.graphics.Color
import android.graphics.Shader
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import com.example.drawrun.R
import com.example.drawrun.ui.common.BaseActivity
import com.example.drawrun.data.repository.UserRepository
import com.example.drawrun.utils.RetrofitInstance
import com.example.drawrun.viewmodel.user.RunningStatViewModel
import com.example.drawrun.viewmodel.user.RunningStatViewModelFactory

class RunningStatActivity : BaseActivity() {

    private lateinit var totalDistanceTextView: TextView
    private lateinit var totalTimeTextView: TextView
    private lateinit var averageHeartbeatTextView: TextView
    private lateinit var averagePaceTextView: TextView



    // ✅ ViewModel 직접 연결
    private val runningStatViewModel: RunningStatViewModel by viewModels {
        RunningStatViewModelFactory(UserRepository(RetrofitInstance.UserApi(this)))
    }

    override fun getLayoutId(): Int = R.layout.activity_running_stat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_running_stat)
        setupBottomNavigation()
        setActiveTab(R.id.navProfile) // ✅ Profile 탭 활성화

        val pageTitleTextView = findViewById<TextView>(R.id.runningStatTitleTextView)

//        val customFont = ResourcesCompat.getFont(this, R.font.praise_regular)
//        pageTitleTextView.apply {
//            typeface = customFont
//            text = "Draw Run"
//            setTextColor(Color.WHITE)
//            textSize = 50f
//        }

        // 텍스트 그라데이션 적용
        applyTextGradient(findViewById(R.id.runningStatTitleTextView))

        val tvWelcomeMessage = findViewById<TextView>(R.id.runningStatTitleTextView)
        val customFont = ResourcesCompat.getFont(this, R.font.praise_regular)
        tvWelcomeMessage.typeface = customFont

        // ✅ TextView 연결
        totalDistanceTextView = findViewById(R.id.totalDistanceTextView)
        totalTimeTextView = findViewById(R.id.totalTimeTextView)
        averageHeartbeatTextView = findViewById(R.id.averageHeartbeatTextView)
        averagePaceTextView = findViewById(R.id.averagePaceTextView)
//        averageCadenceTextView = findViewById(R.id.averageCadenceTextView)
//        longestStreakTextView = findViewById(R.id.longestStreakTextView)
//        currentStreakTextView = findViewById(R.id.currentStreakTextView)

        // ✅ 데이터 가져오기
        runningStatViewModel.fetchRunningStat()

        // ✅ ViewModel 관찰 (UI 업데이트)
//        runningStatViewModel.runningStat.observe(this) { stat ->
//            totalDistanceTextView.text = "총 거리: ${stat.data.totalDistanceKm} km"
//            totalTimeTextView.text = "총 시간: ${stat.data.totalTimeS} 초"
//            averageHeartbeatTextView.text = "평균 심박수: ${stat.data.averageHeartbeat} BPM"
//            averagePaceTextView.text = "평균 페이스: ${stat.data.averagePaceS} 초/km"
//            averageCadenceTextView.text = "평균 케이던스: ${stat.data.averageCadence} 회/분"
//            longestStreakTextView.text = "최장 연속 러닝: ${stat.data.longestStreak} 일"
//            currentStreakTextView.text = "현재 연속 러닝: ${stat.data.currentStreak} 일"
//        }

        runningStatViewModel.runningStat.observe(this) { stat ->
            totalTimeTextView.text = String.format("%02d:%02d:%02d",
                stat.data.totalTimeS / 3600,
                (stat.data.totalTimeS % 3600) / 60,
                stat.data.totalTimeS % 60)

            averagePaceTextView.text = String.format("%02d' %02d\"",
                (stat.data.averagePaceS / 60).toInt(),
                (stat.data.averagePaceS % 60).toInt())

            totalDistanceTextView.text = String.format("%.2f km", stat.data.totalDistanceKm)
            averageHeartbeatTextView.text = "${stat.data.averageHeartbeat.toInt()}bpm"
        }

        // ✅ 오류 처리
        runningStatViewModel.errorState.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }

    }
    private fun applyTextGradient(textView: TextView) {
        textView.post {
            val textWidth = textView.width.toFloat()
            val gradient = android.graphics.LinearGradient(
                0f, 0f, textWidth, 0f,
                intArrayOf(Color.parseColor("#56FF4A"), Color.parseColor("#50F348")),
                null,
                Shader.TileMode.CLAMP
            )
            textView.paint.shader = gradient
            textView.invalidate()
        }
    }
}
