package com.example.drawrun.data.api

import com.example.drawrun.data.model.UserResponse
import java.util.Date

// api 가 없기 때문에 object 추가 나중에 지우면 됨 !!
object UserMockData {
    fun getMockUserData(): UserResponse {
        return UserResponse(
            userId = "jiin4083",
            userName = "유지인",
            userNickname = "징이",
            userEmail = "yujin@example.com",
            profileImgUrl = "https://example.com/profile.png",
            socialType = "kakao",
            socialId = "kakao_12345678",
            badgeId = 101,
            createdDate = Date(),  // 현재 시간 사용
            updateDate = Date(),  // 현재 시간 사용
            isDeleted = false
        )
    }
}