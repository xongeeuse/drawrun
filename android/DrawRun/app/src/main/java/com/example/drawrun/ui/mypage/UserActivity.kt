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

        val userNameTextView: TextView = findViewById(R.id.userNameTextView)
        val userProfileImageView: ImageView = findViewById(R.id.userProfileImageView)
        val settingsIcon: ImageView = findViewById(R.id.settingsIcon)
        val badgeIcon: ImageView = findViewById(R.id.badgeIcon)
        val myArtCustomIcon: ImageView = findViewById(R.id.myartcustomIcon)

        observeUserData(userNameTextView, userProfileImageView)

        settingsIcon.setOnClickListener { navigateToSettings() }
        badgeIcon.setOnClickListener {
            val userName = userViewModel.userData.value?.data?.nickname
            navigateToBadge(userName)
        }

        myArtCustomIcon.setOnClickListener {
            val userName = userViewModel.userData.value?.data?.nickname
            navigateToMyArtCustom(userName)
        }

        applyTextGradient(findViewById(R.id.pageTitleTextView))

        // ✅ 수정된 API 호출 메서드 실행
        userViewModel.fetchUserInfo()
    }


    // ✅ ViewModel 데이터 관찰 및 UI 업데이트
    private fun observeUserData(userNameTextView: TextView, userProfileImageView: ImageView) {
        userViewModel.userData.observe(this, Observer { response ->
            val userData = response.data // ✅ 단일 객체이므로 바로 접근 가능

            userNameTextView.text = userData.nickname

            Glide.with(this)
                .load(userData.profileImgUrl ?: R.drawable.ic_default_profile)
                .placeholder(R.drawable.ic_default_profile)
                .into(userProfileImageView)
        })



        userViewModel.errorState.observe(this, Observer { errorMessage ->
            userNameTextView.text = errorMessage
        })

        userViewModel.loadingState.observe(this, Observer { isLoading ->
            // 로딩 UI 처리
        })
    }

    private fun navigateToSettings() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    private fun navigateToBadge(userName: String?) {
        val intent = Intent(this, BadgeActivity::class.java)
        intent.putExtra("USER_NAME", userName ?: "사용자")
        startActivity(intent)
    }

    private fun navigateToMyArtCustom(userName: String?) {
        val intent = Intent(this, MyArtCustomActivity::class.java)
        intent.putExtra("USER_NAME", userName ?: "사용자")
        startActivity(intent)
    }

    private fun applyTextGradient(textView: TextView) {
        textView.post {
            val textWidth = textView.width.toFloat()
            val gradient = android.graphics.LinearGradient(
                0f, 0f, textWidth, 0f,
                intArrayOf(
                    Color.parseColor("#56FF4A"),
                    Color.parseColor("#50F348")
                ),
                null,
                Shader.TileMode.CLAMP
            )
            textView.paint.shader = gradient
            textView.invalidate()
        }
    }
}
