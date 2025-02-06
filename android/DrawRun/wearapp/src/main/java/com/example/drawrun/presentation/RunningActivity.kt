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
                // 카운트다운이 끝나면 자동으로 NavigationScreen으로 이동
                finish()  // RunningActivity 종료
                startActivity(Intent(this, NavigationActivity::class.java))
            })
        }
    }
}
