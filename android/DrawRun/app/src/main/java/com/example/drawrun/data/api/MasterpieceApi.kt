package com.example.drawrun.data.api

import com.example.drawrun.data.dto.request.masterpiece.MasterpieceCompleteRequest
import com.example.drawrun.data.dto.request.masterpiece.MasterpieceJoinRequest
import com.example.drawrun.data.dto.request.masterpiece.MasterpieceSaveRequest
import com.example.drawrun.data.dto.response.masterpiece.MasterpieceDetailResponse
import com.example.drawrun.data.dto.response.masterpiece.MasterpieceListResponse
import com.example.drawrun.data.dto.response.masterpiece.SectionInfo
import com.example.drawrun.data.dto.response.masterpiece.SectionInfoResponse
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

    @GET("masterpiece/pathlist/{masterpieceBoardId}")
    suspend fun getMasterpieceSectionInfo(
        @Path("masterpieceBoardId") masterpieceBoardId: Int
    ): Response<List<SectionInfo>>

    @POST("masterpiece/join")
    suspend fun joinMasterpiece(@Body request: MasterpieceJoinRequest): Int

    @POST("masterpiece/complete")
    suspend fun completeMasterpiece(@Body request: MasterpieceCompleteRequest): Int
}