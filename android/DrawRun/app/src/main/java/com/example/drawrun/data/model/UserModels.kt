package com.example.drawrun.data.model

import java.util.Date

data class UserResponse (
    val userId: String,
    val userName: String,
    val userNickname: String,
    val userEmail: String,
    val profileImgUrl: String?,
    val socialType: String?,
    val socialId: String?,
    val badgeId: Int?,
    val createdDate: Date,
    val updateDate: Date,
    val isDeleted: Boolean
)