package com.example.drawrun.ui.user

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.drawrun.R
import com.example.drawrun.data.model.UserResponse
import com.example.drawrun.ui.common.BaseActivity
import com.example.drawrun.utils.MockRetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.graphics.LinearGradient
import android.graphics.Shader
import com.example.drawrun.SettingsActivity


class UserActivity : BaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_user  // UserActivity 전용 레이아웃 파일
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val userNameTextView: TextView = findViewById(R.id.userNameTextView)
        val userProfileImageView: ImageView = findViewById(R.id.userProfileImageView)

        // Mock 데이터 가져오기
        MockRetrofitInstance.api.getUserData().enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                val userData = response.body()
                if (userData != null) {
                    // 이름과 프로필 이미지
                    userNameTextView.text = userData.userNickname
                    Glide.with(this@UserActivity)
                        .load(userData.profileImgUrl)
                        .placeholder(R.drawable.ic_default_profile) // 기본 이미지
                        .into(userProfileImageView)
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                // 에러 처리
            }
        })

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
    }




}
