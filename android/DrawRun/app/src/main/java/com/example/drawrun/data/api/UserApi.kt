package com.example.drawrun.data.api

import com.example.drawrun.data.dto.response.user.GetMyArtCustomResponse
import com.example.drawrun.data.dto.response.user.GetMyInfoResponse
import com.example.drawrun.data.model.MypageResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

//http://localhost:8080/api/v1/
interface UserApi {
//    @GET("mypage")
//    suspend fun getMyPageData(): MypageResponse

    @GET("user/mypage")  // 마이페이지 조회
    suspend fun getMyInfo(): Response<GetMyInfoResponse>

    @GET("user/art") // 아트컬렉션 조회
    suspend fun getMyArtCustom(): Response<GetMyArtCustomResponse>
}