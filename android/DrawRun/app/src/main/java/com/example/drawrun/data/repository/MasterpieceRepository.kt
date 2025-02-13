package com.example.drawrun.data.repository

import android.util.Log
import com.example.drawrun.data.api.MasterpieceApi
import com.example.drawrun.data.dto.request.masterpiece.MasterpieceSaveRequest
import com.example.drawrun.data.dto.response.masterpiece.MasterpieceListResponse

class MasterpieceRepository (private val api: MasterpieceApi) {
    // 응답으로 0 or 걸작 게시글 PK
    suspend fun saveMasterpiece(request: MasterpieceSaveRequest): Int {
        Log.d("MasterpieceRepository", "Sending request: $request")
        return api.saveMasterpiece(request)
    }

    suspend fun getMasterpieceList(): Result<MasterpieceListResponse> {
        return try {
            val response = api.getMasterpieceList()
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error fetching masterpiece list: ${response.code()}"))
            }
        } catch (e: Exception) {
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