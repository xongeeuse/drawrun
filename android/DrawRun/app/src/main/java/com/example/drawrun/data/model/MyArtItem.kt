package com.example.drawrun.data.model

data class MyArtItem(
    val imageRes: Int,  // 이미지 리소스 ID (R.drawable.xxx)
    val title: String,   // 코스 이름
    val info: String     // 코스 정보 (예: 거리, 시간 등)
)