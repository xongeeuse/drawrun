package com.example.drawrun.data.repository

import com.example.drawrun.data.api.UserApi
import com.example.drawrun.data.model.MypageResponse

class UserRepository(private val api: UserApi) {

    // API를 호출하여 데이터 반환
    suspend fun getUserData(): MypageResponse {
        return api.getMyPageData()
    }
}
