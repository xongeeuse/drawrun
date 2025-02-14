package com.example.drawrun.data.api

import com.example.drawrun.data.dto.request.masterpiece.MasterpieceSaveRequest
import com.example.drawrun.data.dto.response.masterpiece.MasterpieceDetailResponse
import com.example.drawrun.data.dto.response.masterpiece.MasterpieceListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MasterpieceApi {
    @POST("masterpiece/save")
    suspend fun saveMasterpiece(@Body request: MasterpieceSaveRequest): Int

    @GET("masterpiece/list")
    suspend fun getMasterpieceList(): Response<MasterpieceListResponse>

    @GET("masterpiece/list")
    suspend fun getMasterpieceListByArea(@Query("area") area: String): Response<MasterpieceListResponse>

    @GET("masterpiece/search/{masterpieceBoardId}")
    suspend fun getMasterpieceDetail(
        @Path("masterpieceBoardId") masterpieceBoardId: Int // Path Parameter로 ID 전달
    ): Response<MasterpieceDetailResponse>
}