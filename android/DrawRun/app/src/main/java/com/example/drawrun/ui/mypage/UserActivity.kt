package com.example.drawrun.ui.mypage

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.drawrun.R
import com.example.drawrun.ui.common.BaseActivity
import android.graphics.Shader
import com.example.drawrun.utils.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class UserActivity : BaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_user  // UserActivity 전용 레이아웃 파일
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val userNameTextView: TextView = findViewById(R.id.userNameTextView)
        val userProfileImageView: ImageView = findViewById(R.id.userProfileImageView)

        // 실제 API 호출 (코루틴 사용)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 백엔드에서 사용자 데이터 가져오기
                val userData = RetrofitInstance.UserApi(this@UserActivity).getMyPageData()

                // UI 업데이트 (메인 스레드에서 실행)
                withContext(Dispatchers.Main) {
                    userNameTextView.text = userData.userNickname
                    Glide.with(this@UserActivity)
                        .load(userData.profileImgUrl)
                        .placeholder(R.drawable.ic_default_profile)
                        .into(userProfileImageView)
                }
            } catch (e: Exception) {
                // 에러 처리 (UI에서 표시)
                withContext(Dispatchers.Main) {
                    userNameTextView.text = "데이터를 불러오지 못했습니다."
                }
            }
        }

        // 설정 페이지 이동
        val settingsIcon: ImageView = findViewById(R.id.settingsIcon)
        settingsIcon.setOnClickListener{
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    
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


        // badge activity 이동

        // ImageView 가져오기
        val badgeIcon: ImageView = findViewById(R.id.badgeIcon)

        // 클릭 이벤트 설정
        badgeIcon.setOnClickListener {
            // BadgeActivity로 이동
            val intent = Intent(this, BadgeActivity::class.java)
            startActivity(intent)
        }
    }




}
