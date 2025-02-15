package com.example.drawrun.data.repository

import android.content.Context
import android.util.Log
import com.example.drawrun.data.api.RunRecordApi
import com.example.drawrun.data.dto.request.runrecord.RunRecordRequest
import com.example.drawrun.utils.RetrofitInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class RunRecordRepository(private val api: RunRecordApi) {

    suspend fun saveRunRecord(request: RunRecordRequest): Flow<Result<Unit>> = flow {
        try {
            Log.d("RunRecordRepository", "📡 러닝 기록 저장 요청: $request")
            val response = api.saveRunRecord(request)

            if (response.isSuccessful) {
                Log.d("RunRecordRepository", "✅ 러닝 기록 저장 성공")
                emit(Result.success(Unit))
            } else {
                Log.e("RunRecordRepository", "🚨 러닝 기록 저장 실패 - 응답 코드: ${response.code()}, 응답 메시지: ${response.errorBody()?.string()}")
                emit(Result.failure(Exception("저장 실패: ${response.code()} - ${response.message()}")))
            }
        } catch (e: HttpException) {
            Log.e("RunRecordRepository", "🚨 HTTP 오류 발생: ${e.response()?.errorBody()?.string()}")
            emit(Result.failure(Exception("HTTP 오류: ${e.code()} - ${e.message()}")))
        } catch (e: IOException) {
            Log.e("RunRecordRepository", "🚨 네트워크 오류 발생: ${e.message}")
            emit(Result.failure(Exception("네트워크 오류: ${e.message}")))
        } catch (e: Exception) {
            Log.e("RunRecordRepository", "🚨 알 수 없는 오류 발생: ${e.message}")
            emit(Result.failure(Exception("알 수 없는 오류: ${e.message}")))
        }
    }
}