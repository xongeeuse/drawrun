package com.example.drawrun.data.repository

import android.util.Log
import com.example.drawrun.data.api.MasterpieceApi
import com.example.drawrun.data.dto.request.masterpiece.MasterpieceSaveRequest
import com.example.drawrun.data.dto.response.masterpiece.Masterpiece
import com.example.drawrun.data.dto.response.masterpiece.MasterpieceListResponse

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
}