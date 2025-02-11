package com.example.drawrun.ui.mypage

import android.graphics.Color
import android.graphics.Shader
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.drawrun.R

class MyArtCustomActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myartcustom)

        // DrawRun 로고 그라데이션 설정
        applyTextGradient(findViewById(R.id.badgeTitleTextView))
    }


    // ✅ 텍스트 그라데이션 설정 함수
    private fun applyTextGradient(textView: TextView) {
        textView.post {
            val textWidth = textView.width.toFloat()
            val gradient = android.graphics.LinearGradient(
                0f, 0f, textWidth, 0f,
                intArrayOf(
                    Color.parseColor("#56FF4A"), // 시작 색상
                    Color.parseColor("#50F348")  // 끝 색상
                ),
                null,
                Shader.TileMode.CLAMP
            )
            textView.paint.shader = gradient
            textView.invalidate()
        }
    }


}