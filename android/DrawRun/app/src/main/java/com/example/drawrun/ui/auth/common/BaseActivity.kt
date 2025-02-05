package com.example.drawrun.ui.common

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.drawrun.R
import com.example.drawrun.MainActivity
import com.example.drawrun.ui.user.UserActivity

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())  // 각 액티비티에서 레이아웃 제공
        setupBottomNavigation()        // 하단바 설정
    }

    // 각 액티비티에서 고유 레이아웃 리소스를 제공
    abstract fun getLayoutId(): Int
    // 우선 구현된 userActivity 한정 mainActivity 건드리면 충돌날까봐 안함 추후 설정
    private fun setupBottomNavigation() {
        findViewById<ImageView>(R.id.navHome).setOnClickListener {
            if (this !is UserActivity) {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }

        findViewById<ImageView>(R.id.navDocu).setOnClickListener {
            if (this !is UserActivity) {
                startActivity(Intent(this, UserActivity::class.java))
            }
        }

        findViewById<ImageView>(R.id.navProfile).setOnClickListener {
            if (this !is UserActivity) {
                startActivity(Intent(this, UserActivity::class.java))
            }
        }

        findViewById<ImageView>(R.id.navLanking).setOnClickListener {
            if (this !is UserActivity) {
                startActivity(Intent(this, UserActivity::class.java))
            }
        }
    }
}
