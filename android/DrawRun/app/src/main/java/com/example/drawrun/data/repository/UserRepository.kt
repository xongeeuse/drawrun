package com.example.drawrun.data.repository

import android.util.Log
import com.example.drawrun.data.api.UserApi
import com.example.drawrun.data.dto.response.user.GetBookMarkResponse
import com.example.drawrun.data.dto.response.user.GetMyArtCustomResponse
import com.example.drawrun.data.dto.response.user.GetMyInfoResponse
import com.example.drawrun.data.dto.response.user.GetUserStatResponse
import com.example.drawrun.data.model.MypageResponse
import retrofit2.HttpException

class UserRepository(private val api: UserApi) {

    // API를 호출하여 데이터 반환
//    suspend fun getUserData(): MypageResponse {
//        return api.getMyPageData()
//    }

    suspend fun getUserInfo(): Result<GetMyInfoResponse> {
        return try {
            val response = api.getMyInfo()

            // ✅ HTTP 응답이 성공적인지 확인
            if (!response.isSuccessful) {
                return Result.failure(RuntimeException("HTTP 오류: ${response.code()} ${response.message()}"))
            }

            // ✅ body가 null이면 예외 발생
            val responseBody = response.body()
                ?: return Result.failure(RuntimeException("응답 데이터가 없음"))

            // ✅ API가 isSuccess=false를 반환한 경우 처리
            if (!responseBody.isSuccess) {
                return Result.failure(RuntimeException("API 요청 실패: ${responseBody.message} (코드: ${responseBody.code})"))
            }

            Result.success(responseBody) // ✅ 성공 시 데이터 반환
        } catch (e: HttpException) {
            Log.e("UserRepository", "HTTP 오류 발생: ${e.code()} ${e.message()}")
            Result.failure(RuntimeException("HTTP 오류 발생: ${e.code()}"))
        } catch (e: Exception) {
            Log.e("UserRepository", "알 수 없는 오류 발생: ${e.message}")
            Result.failure(RuntimeException("알 수 없는 오류 발생: ${e.message}"))
        }
    }

    suspend fun getMyArtCustomInfo(): GetMyArtCustomResponse {
        val response = api.getMyArtCustom()
        if (response.isSuccessful) {
            return response.body() ?: throw IllegalStateException("Response body is null")
        } else {
            throw HttpException(response)
        }
    }

    suspend fun getBookmarkInfo(): Result<GetBookMarkResponse> {
        return try {
            val response = api.getBookmarkInfo()

            // ✅ HTTP 응답이 성공적인지 확인
            if (!response.isSuccessful) {
                return Result.failure(RuntimeException("HTTP 오류: ${response.code()} ${response.message()}"))
            }

            // ✅ body가 null이면 예외 발생
            val responseBody = response.body()
                ?: return Result.failure(RuntimeException("응답 데이터가 없음"))

            Result.success(responseBody) // ✅ 성공 시 데이터 반환
        } catch (e: HttpException) {
            Log.e("UserRepository", "HTTP 오류 발생: ${e.code()} ${e.message()}")
            Result.failure(RuntimeException("HTTP 오류 발생: ${e.code()}"))
        } catch (e: Exception) {
            Log.e("UserRepository", "알 수 없는 오류 발생: ${e.message}")
            Result.failure(RuntimeException("알 수 없는 오류 발생: ${e.message}"))
        }
    }

    suspend fun getUserStatInfo(): Result<GetUserStatResponse> {
        return try {
            val response = api.getUserStatInfo()

            // ✅ HTTP 응답이 성공적인지 확인
            if (!response.isSuccessful) {
                return Result.failure(RuntimeException("HTTP 오류: ${response.code()} ${response.message()}"))
            }

            // ✅ body가 null이면 예외 발생
            val responseBody = response.body()
                ?: return Result.failure(RuntimeException("응답 데이터가 없음"))

            // ✅ API에서 isSuccess=false를 반환한 경우 처리
            if (!responseBody.isSuccess) {
                return Result.failure(RuntimeException("API 요청 실패: ${responseBody.message} (코드: ${responseBody.code})"))
            }

            Result.success(responseBody) // ✅ 정상적으로 데이터를 반환
        } catch (e: HttpException) {
            Log.e("UserRepository", "HTTP 오류 발생: ${e.code()} ${e.message}")
            Result.failure(RuntimeException("HTTP 오류 발생: ${e.code()}"))
        } catch (e: Exception) {
            Log.e("UserRepository", "알 수 없는 오류 발생: ${e.message}")
            Result.failure(RuntimeException("알 수 없는 오류 발생: ${e.message}"))
        }
    }

}
