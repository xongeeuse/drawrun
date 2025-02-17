package com.example.drawrun.data.repository

import android.util.Log
import com.example.drawrun.data.api.MasterpieceApi
import com.example.drawrun.data.dto.request.masterpiece.MasterpieceCompleteRequest
import com.example.drawrun.data.dto.request.masterpiece.MasterpieceJoinRequest
import com.example.drawrun.data.dto.request.masterpiece.MasterpieceSaveRequest
import com.example.drawrun.data.dto.response.masterpiece.Masterpiece
import com.example.drawrun.data.dto.response.masterpiece.MasterpieceDetailResponse
import com.example.drawrun.data.dto.response.masterpiece.MasterpieceListResponse
import com.example.drawrun.data.dto.response.masterpiece.SectionInfoResponse
import retrofit2.Response

class MasterpieceRepository (private val api: MasterpieceApi) {
    // 응답으로 0 or 걸작 게시글 PK
    suspend fun saveMasterpiece(request: MasterpieceSaveRequest): Int {
        Log.d("MasterpieceRepository", "Sending request: $request")
        return api.saveMasterpiece(request)
    }


    suspend fun getMasterpieceList(): Result<List<Masterpiece>> {
        return try {
            val response = api.getMasterpieceList() // Retrofit API 호출
            if (response.isSuccessful) {
                // 성공 시, 응답 데이터를 Result로 래핑하여 반환
                Result.success(response.body() ?: emptyList())
            } else {
                // 실패 시, 예외를 포함한 Result 반환
                Result.failure(Exception("Error fetching masterpiece list: ${response.code()}"))
            }
        } catch (e: Exception) {
            // 네트워크 또는 기타 예외 처리
            Result.failure(e)
        }
    }


    suspend fun getMasterpieceListByArea(area: String): Result<MasterpieceListResponse> {
        return try {
            val response = api.getMasterpieceListByArea(area)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error fetching masterpiece list by area: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun getMasterpieceDetail(masterpieceBoardId: Int): Response<MasterpieceDetailResponse> {
        return api.getMasterpieceDetail(masterpieceBoardId)
    }

    suspend fun getMasterpieceSectionInfo(masterpieceBoardId: Int): Result<SectionInfoResponse> {
        return try {
            val response = api.getMasterpieceSectionInfo(masterpieceBoardId)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error fetching section info: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun joinMasterpiece(request: MasterpieceJoinRequest): Result<Boolean> {
        return try {
            val response = api.joinMasterpiece(request)
            Log.d("MasterpieceRepository", "Sending join request with segId: ${request.masterpieceSegId}")
            Log.d("MasterpieceRepository", "Join API response: $response")
            if (response == 1) {
                Result.success(true)
            } else {
                Result.failure(Exception("Join failed: API returned $response"))
            }
        } catch (e: Exception) {
            Log.e("MasterpieceRepository", "Error joining masterpiece: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun completeMasterpiece(request: MasterpieceCompleteRequest): Result<Boolean> {
        return try {
            val response = api.completeMasterpiece(request)
            Log.d("MasterpieceRepository", "Sending complete request with segId: ${request.masterpieceSegId}")
            Log.d("MasterpieceRepository", "Complete API response: $response")
            if (response == 1) {
                Result.success(true)
            } else {
                Result.failure(Exception("Complete failed: API returned $response"))
            }
        } catch (e: Exception) {
            Log.e("MasterpieceRepository", "Error completing masterpiece: ${e.message}")
            Result.failure(e)
        }
    }
}