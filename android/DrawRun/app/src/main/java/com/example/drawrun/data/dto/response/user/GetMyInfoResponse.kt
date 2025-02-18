package com.example.drawrun.data.dto.response.user

data class GetMyInfoResponse(
    val isSuccess: Boolean,
    val message: String,
    val code: Int,
    val data: UserData,
)

data class UserData(
    val userPK: Int,
    val nickname: String,
    val profileImgUrl: String?,
    val history: List<UserHistory>
)

data class UserHistory(
    val pathImgUrl: String,
    val createDate: String,
    val distance: Double,
    val time: Int,
    val pace: Int,
    val heartbeat: Int,
    val cadence: Int
)