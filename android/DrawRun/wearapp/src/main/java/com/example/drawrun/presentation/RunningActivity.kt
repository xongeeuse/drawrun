package com.example.drawrun.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.drawrun.presentation.NavigationActivity
import com.example.drawrun.presentation.ui.RunningScreen

class RunningActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RunningScreen(onCountdownFinished = {
                // 데이터를 담아 인텐트 전송
                val intent = Intent(this, NavigationActivity::class.java).apply {
                    putExtra("distanceToNextTurn", 100.0)  // 예시 데이터
                    putExtra("voiceInstruction", "안내 시작")
                    putExtra("totalDistance", 1000.0)
                    putExtra("distanceRemaining", 500.0)
                }
                // 카운트다운이 끝나면 자동으로 NavigationScreen으로 이동
                finish()  // RunningActivity 종료
                startActivity(Intent(this, NavigationActivity::class.java))
            })
        }
    }
}
