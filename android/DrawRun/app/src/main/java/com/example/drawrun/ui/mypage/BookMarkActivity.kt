package com.example.drawrun.ui.mypage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.drawrun.R
import com.example.drawrun.ui.mypage.fragment.BookMarkFragment

class BookMarkActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark)

        // ✅ 만약 Fragment가 동적으로 추가되지 않았다면 수동으로 추가
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, BookMarkFragment()) // 🔥 BookMarkFragment 추가
                .commit()
        }
    }
}
