package com.example.drawrun.data.repository

import android.util.Log
import com.example.drawrun.data.api.SearchApi
import com.example.drawrun.data.dto.response.search.SearchResponse

// 러닝 코스 검색용 레포지토리
class SearchRepository(private val api: SearchApi) {
    
    // 키워드로 검색
    suspend fun searchByKeyword(keyword: String): Result<SearchResponse> {
        return try {
            Log.d("SearchSearch", "API 호출 했습니다.")
            val response = api.searchCoursesByKeyword(keyword)
            if (response.isSuccessful) {
                Log.d("SearchSearch", "API 호출 성공.")
                Result.success(response.body() ?: throw Exception("Response body is null"))
            } else {
                Log.d("SearchSearch", "API 호출 실패")
                Result.failure(Exception("Search failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.d("SearchSearch", "API 호출 뭔가 실패")
            Result.failure(e)
        }
    }
    
    // 지역으로 검색
    suspend fun searchByLocation(location: String): Result<SearchResponse> {
        return try {
            val response = api.searchCoursesByLocation(location)
            if (response.isSuccessful) {
                Result.success(response.body() ?: throw Exception("Response body is null"))
            } else {
                Result.failure(Exception("Search failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
