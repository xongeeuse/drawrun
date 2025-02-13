package com.example.drawrun.data.repository

import com.example.drawrun.data.api.RunRecordApi
import com.example.drawrun.data.dto.request.runrecord.RunRecordRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RunRecordRepository(private val api: RunRecordApi) {

    suspend fun saveRunRecord(request: RunRecordRequest): Flow<Result<Unit>> = flow {
        try {
            val response = api.saveRunRecord(request)
            if (response.isSuccessful) {
                emit(Result.success(Unit))
            } else {
                emit(Result.failure(Exception("저장 실패: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
