package com.example.drawrun.utils

import com.example.drawrun.data.api.UserApi
import com.example.drawrun.data.api.UserMockData
import com.example.drawrun.data.model.UserResponse
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object MockRetrofitInstance {
    private const val BASE_URL = "http://13.124.222.21:8081/api/v1/"

    // Mock API 전용 RetrofitInstance
    val api: UserApi by lazy {
        MockUserAdapter()
    }

    private class MockUserAdapter : UserApi {
        override fun getUserData(): Call<UserResponse> {
            return object : Call<UserResponse> {
                override fun enqueue(callback: Callback<UserResponse>) {
                    // 비동기로 Mock 데이터 반환
                    callback.onResponse(this, Response.success(UserMockData.getMockUserData()))
                }

                override fun execute(): Response<UserResponse> = Response.success(UserMockData.getMockUserData())

                // 필수 메서드들 구현
                override fun isExecuted() = false
                override fun cancel() {}
                override fun isCanceled() = false
                override fun request() = okhttp3.Request.Builder().build()
                override fun clone(): Call<UserResponse> = this

                // 새로 추가된 timeout 메서드 구현
                override fun timeout(): Timeout = Timeout.NONE
            }
        }
    }
}
