package com.example.drawrun.ui.mypage

import android.graphics.Color
import android.graphics.Shader
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.drawrun.R

class BadgeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_badge)

        // drawrun 로고 그라데이션.. xml에서는 못함 ㅠ
        val pageTitleTextView: TextView = findViewById(R.id.pageTitleTextView)
        val textWidth = pageTitleTextView.width.toFloat()
        val gradient = android.graphics.LinearGradient(
            0f, 0f, textWidth, 0f,
            intArrayOf(
                Color.parseColor("#56FF4A"), // 시작 색상
                Color.parseColor("#50F348")  // 끝 색상
            ),
            null,
            Shader.TileMode.CLAMP
        )
        pageTitleTextView.paint.shader = gradient
        pageTitleTextView.invalidate()

        val userNameTextView: TextView = findViewById(R.id.userNameTextView)

    }
}