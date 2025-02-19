package com.example.drawrun.ui.mypage

import android.graphics.Color
import android.graphics.Shader
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.drawrun.R
import com.example.drawrun.ui.common.BaseActivity
import com.example.drawrun.ui.mypage.fragment.MyArtCustomFragment

class MyArtCustomActivity : BaseActivity(){
    override fun getLayoutId(): Int = R.layout.activity_myartcustom

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myartcustom)

        setupBottomNavigation()  // ✅ 하단바 설정
        setActiveTab(R.id.navProfile)  // ✅ Profile 탭 활성화

        // DrawRun 로고 그라데이션 설정
        applyTextGradient(findViewById(R.id.myArtCustomTitleTextView))

        val tvWelcomeMessage = findViewById<TextView>(R.id.myArtCustomTitleTextView)
        val customFont = ResourcesCompat.getFont(this, R.font.praise_regular)
        tvWelcomeMessage.typeface = customFont


        // Fragment 추가
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, MyArtCustomFragment())
                .commit()
        }

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