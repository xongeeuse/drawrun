package com.example.drawrun.ui.common

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.drawrun.R
import com.example.drawrun.MainActivity
import com.example.drawrun.ui.masterpiece.MasterpieceActivity
import com.example.drawrun.ui.mypage.UserActivity
import com.example.drawrun.ui.search.SearchActivity
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())  // ✅ 레이아웃 설정
        setupBottomNavigation()
    }

    // 각 액티비티에서 제공할 레이아웃 ID
    abstract fun getLayoutId(): Int

    override fun onResume() {
        super.onResume()
        setupBottomNavigation()  // ✅ Activity가 재개될 때 실행
    }

    // ✅ 네비게이션 바 설정 (하위 액티비티에서 직접 호출)
    fun setupBottomNavigation() {
        val homeButton = findViewById<LinearLayout>(R.id.navHome)?.getChildAt(0) as? ImageView
        val docuButton = findViewById<LinearLayout>(R.id.navDocu)?.getChildAt(0) as? ImageView
        val searchButton = findViewById<LinearLayout>(R.id.navSearch)?.getChildAt(0) as? ImageView
        val profileButton = findViewById<LinearLayout>(R.id.navProfile)?.getChildAt(0) as? ImageView

        if (homeButton == null) Log.e("BaseActivity", "⚠️ navHome is NULL!")
        if (docuButton == null) Log.e("BaseActivity", "⚠️ navDocu is NULL!")
        if (searchButton == null) Log.e("BaseActivity", "⚠️ navSearch is NULL!")
        if (profileButton == null) Log.e("BaseActivity", "⚠️ navProfile is NULL!")

        homeButton?.setOnClickListener {
            Log.d("BaseActivity", "🏠 Home 버튼 클릭됨")
            startActivity(Intent(this, MainActivity::class.java))
        }

        docuButton?.setOnClickListener {
            Log.d("BaseActivity", "📄 Document 버튼 클릭됨")
            startActivity(Intent(this, MasterpieceActivity::class.java))
        }

        searchButton?.setOnClickListener {
            Log.d("BaseActivity", "🔍 Search 버튼 클릭됨")
            startActivity(Intent(this, SearchActivity::class.java))
        }

        profileButton?.setOnClickListener {
            Log.d("BaseActivity", "👤 Profile 버튼 클릭됨")
            startActivity(Intent(this, UserActivity::class.java))
        }

        when (this) {
            is MainActivity -> setActiveTab(R.id.navHome)
            is MasterpieceActivity -> setActiveTab(R.id.navDocu)
            is SearchActivity -> setActiveTab(R.id.navSearch)
            is UserActivity -> setActiveTab(R.id.navProfile)
        }
    }

    fun setActiveTab(activeTabId: Int) {
        val navHome = findViewById<LinearLayout>(R.id.navHome)
        val navDocu = findViewById<LinearLayout>(R.id.navDocu)
        val navSearch = findViewById<LinearLayout>(R.id.navSearch)
        val navProfile = findViewById<LinearLayout>(R.id.navProfile)

        val tabs = listOf(navHome, navDocu, navSearch, navProfile)
        tabs.forEach { tab ->
            val isActive = tab.id == activeTabId
            val icon = tab.getChildAt(0) as ImageView
            val text = tab.getChildAt(1) as TextView

            icon.setColorFilter(if (isActive) Color.WHITE else Color.GRAY)
            text.setTextColor(if (isActive) Color.WHITE else Color.GRAY)
        }
    }

}
