package com.example.drawrun.ui.mypage

import android.content.Intent
import android.graphics.Color
import android.graphics.Shader
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.drawrun.R
import com.example.drawrun.data.repository.UserRepository
import com.example.drawrun.ui.common.BaseActivity
import com.example.drawrun.viewmodel.user.UserViewModel
import com.example.drawrun.viewmodel.user.UserViewModelFactory
import com.example.drawrun.utils.RetrofitInstance

class UserActivity : BaseActivity() {

    // UserViewModel 초기화 (viewModels로 ViewModel 인스턴스 생성)
    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(UserRepository(RetrofitInstance.UserApi(this)))
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_user
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        // View 요소 가져오기
        val userNameTextView: TextView = findViewById(R.id.userNameTextView)
        val userProfileImageView: ImageView = findViewById(R.id.userProfileImageView)
        val settingsIcon: ImageView = findViewById(R.id.settingsIcon)
        val badgeIcon: ImageView = findViewById(R.id.badgeIcon)
        val myArtCustomIcon: ImageView = findViewById(R.id.myartcustomIcon)
        // ViewModel을 통해 데이터 가져오기 및 관찰
        observeUserData(userNameTextView, userProfileImageView)

        // 설정 페이지 이동 이벤트
        settingsIcon.setOnClickListener {
            navigateToSettings()
        }

        // 배지 페이지 이동 이벤트
        badgeIcon.setOnClickListener {
            val userName = userViewModel.userData.value?.userNickname
            navigateToBadge(userName)
        }

        // 마이커스텀 페이지 이동 이벤트
        myArtCustomIcon.setOnClickListener {
            val userName = userViewModel.userData.value?.userNickname
            navigateToMyArtCustom(userName)
        }

        // DrawRun 로고 그라데이션 설정
        applyTextGradient(findViewById(R.id.pageTitleTextView))

        // 사용자 데이터 가져오기 요청
        userViewModel.fetchUserData()
    }

    // ✅ ViewModel 데이터 관찰 및 UI 업데이트
    private fun observeUserData(userNameTextView: TextView, userProfileImageView: ImageView) {
        // 사용자 데이터 관찰
        userViewModel.userData.observe(this, Observer { userData ->
            userNameTextView.text = userData.userNickname
            Glide.with(this)
                .load(userData.profileImgUrl)
                .placeholder(R.drawable.ic_default_profile)
                .into(userProfileImageView)
        })

        // 에러 상태 관찰
        userViewModel.errorState.observe(this, Observer { errorMessage ->
            userNameTextView.text = errorMessage
        })

        // 로딩 상태 관찰 (필요 시 추가)
        userViewModel.loadingState.observe(this, Observer { isLoading ->
            // 로딩 상태 처리 (프로그래스바 표시 등)
        })
    }

    // ✅ 설정 페이지로 이동하는 함수
    private fun navigateToSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    // ✅ 배지 페이지로 이동하는 함수
    private fun navigateToBadge(userName: String?) {
        val intent = Intent(this, BadgeActivity::class.java)
        intent.putExtra("USER_NAME", userName ?: "사용자")
        startActivity(intent)
    }

    // ✅ 마이커스텀으로 이동하는 함수
    private fun navigateToMyArtCustom(userName: String?) {
        val intent = Intent(this, MyArtCustomActivity::class.java)
        intent.putExtra("USER_NAME", userName ?: "사용자")
        startActivity(intent)
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
