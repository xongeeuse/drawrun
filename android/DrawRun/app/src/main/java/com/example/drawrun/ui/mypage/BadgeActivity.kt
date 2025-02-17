package com.example.drawrun.ui.mypage

import android.graphics.Color
import android.graphics.Shader
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.drawrun.R
import com.example.drawrun.data.model.BadgeItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class BadgeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_badge)
        // 사용자 이름 가져오기
        val userName = intent.getStringExtra("USER_NAME") ?: "사용자"

        // 사용자 이름 텍스트 설정
        val userNameTextView: TextView = findViewById(R.id.userNameTextView)
        userNameTextView.text = "${userName}님의 뱃지"

        // DrawRun 로고 그라데이션 설정
        applyTextGradient(findViewById(R.id.badgeTitleTextView))

        val badgeContainer: LinearLayout = findViewById(R.id.badgeContainer)
        val badgeList = loadBadgeData()
        fillBadgeContainer(badgeList, badgeContainer)

    }

    // 각 액티비티에서 고유 레이아웃 리소스를 제공
//    override fun getLayoutId(): Int = R.layout.activity_badge

    private fun loadBadgeData(): List<BadgeItem>{
        val inputStream = assets.open("badges.json")
        val json = inputStream.bufferedReader().use {it.readText()}

        val listType = object : TypeToken<List<BadgeItem>>() {}.type
        return Gson().fromJson(json, listType)
    }


    private fun fillBadgeContainer(badgeList: List<BadgeItem>, badgeContainer: LinearLayout) {
        val inflater = LayoutInflater.from(this)

        // 3개씩 행에 배치
        badgeList.chunked(3).forEachIndexed { index, rowItems ->
            val rowLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    // 인덱스를 사용하여 첫 번째 행만 간격 없음
                    setMargins(0, if (index == 0) 0 else 16.dpToPx(), 0, 0)
                }
                gravity = android.view.Gravity.CENTER_HORIZONTAL // 가로 중앙 정렬
            }

            // 각 배지 추가
            rowItems.forEach { badge ->
                val badgeView = inflater.inflate(R.layout.item_badge, rowLayout, false)

                // 배지 이미지 설정
                val badgeImageView = badgeView.findViewById<ImageView>(R.id.badgeImageView)
                val resourceId = resources.getIdentifier(badge.badgeImg, "drawable", packageName)
                Glide.with(this).load(resourceId).into(badgeImageView)

                // 배지 이름과 설명 설정
                badgeView.findViewById<TextView>(R.id.badgeNameTextView).text = badge.badgeName
                badgeView.findViewById<TextView>(R.id.badgeDescriptionTextView).text = badge.badgeDes

                // 뷰 여백 설정 (weight 제거)
                val badgeLayoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(12.dpToPx(), 0, 12.dpToPx(), 0)  // 좌우 여백 조정
                }
                badgeView.layoutParams = badgeLayoutParams

                // 행 레이아웃에 배지 추가
                rowLayout.addView(badgeView)
            }

            // 배지 컨테이너에 행 추가
            badgeContainer.addView(rowLayout)
        }
    }

    // dp를 px로 변환하는 확장 함수
    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()


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

