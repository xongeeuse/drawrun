package com.example.drawrun.data.model

import java.util.Date

data class MypageResponse (
    val userId: String,
    val userNickname: String,
    val profileImgUrl: String?,
    val name: String,
    val userEmail: String
)