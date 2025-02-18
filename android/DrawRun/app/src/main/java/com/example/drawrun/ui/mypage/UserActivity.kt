package com.example.drawrun.ui.mypage

import android.content.Intent
import android.graphics.Color
import android.graphics.Shader
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.drawrun.R
import com.example.drawrun.data.repository.UserRepository
import com.example.drawrun.ui.common.BaseActivity
import com.example.drawrun.ui.mypage.adaptor.RunningHistoryAdapter
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
        val recyclerView: RecyclerView = findViewById(R.id.runningHistoryRecyclerView)
        val settingsIcon: ImageView = findViewById(R.id.settingsIcon)
        val badgeIcon: ImageView = findViewById(R.id.badgeIcon)
        val myArtCustomIcon: ImageView = findViewById(R.id.myartcustomIcon)
        val emptyMessageTextView: TextView = findViewById(R.id.emptyMessageTextView)

        val pageTitleTextView = findViewById<TextView>(R.id.pageTitleTextView)

        val customFont = ResourcesCompat.getFont(this, R.font.praise_regular)
        pageTitleTextView.apply {
            typeface = customFont
            text = "Draw Run"
            setTextColor(Color.WHITE)
            textSize = 50f
        }

        // RecyclerView 설정
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        // UI 데이터 관찰
        observeUserData(userNameTextView, userProfileImageView, recyclerView, emptyMessageTextView)

        // 클릭 이벤트 설정
        settingsIcon.setOnClickListener { navigateToSettings() }
        badgeIcon.setOnClickListener {
            val userName = userViewModel.userData.value?.data?.nickname
            navigateToBadge(userName)
        }
        myArtCustomIcon.setOnClickListener {
            val userName = userViewModel.userData.value?.data?.nickname
            navigateToMyArtCustom(userName)
        }

        // 텍스트 그라데이션 적용
        applyTextGradient(findViewById(R.id.pageTitleTextView))

        // ✅ API 호출 실행
        userViewModel.fetchUserInfo()
        userViewModel.fetchRunningHistory()
    }

    // ✅ ViewModel 데이터 관찰 및 UI 업데이트
    private fun observeUserData(
        userNameTextView: TextView,
        userProfileImageView: ImageView,
        recyclerView: RecyclerView,
        emptyMessageTextView: TextView,
    ) {
        userViewModel.userData.observe(this, Observer { response ->
            val userData = response.data

            userNameTextView.text = userData.nickname
            Glide.with(this)
                .load(userData.profileImgUrl ?: R.drawable.ic_default_profile)
                .placeholder(R.drawable.ic_default_profile)
                .into(userProfileImageView)
        })

        // ✅ RecyclerView 데이터 업데이트
        userViewModel.runningHistory.observe(this, Observer { historyList ->
            if (historyList.isEmpty()) {
                recyclerView.visibility = View.GONE  // ✅ 리스트 숨기기
                emptyMessageTextView.visibility = View.VISIBLE  // ✅ "기록이 없습니다" 메시지 표시
            } else {
                recyclerView.visibility = View.VISIBLE  // ✅ 리스트 보이기
                emptyMessageTextView.visibility = View.GONE  // ✅ 메시지 숨기기
                recyclerView.adapter = RunningHistoryAdapter(historyList)
            }
        })

        userViewModel.errorState.observe(this, Observer { errorMessage ->
            userNameTextView.text = errorMessage
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
                intArrayOf(Color.parseColor("#56FF4A"), Color.parseColor("#50F348")),
                null,
                Shader.TileMode.CLAMP
            )
            textView.paint.shader = gradient
            textView.invalidate()
        }
    }
}
