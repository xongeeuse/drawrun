package com.example.drawrun.data.repository

import android.util.Log
import com.example.drawrun.data.api.MasterpieceApi
import com.example.drawrun.data.dto.request.masterpiece.MasterpieceSaveRequest

class MasterpieceRepository (private val api: MasterpieceApi) {
    // 응답으로 0 or 걸작 게시글 PK
    suspend fun saveMasterpiece(request: MasterpieceSaveRequest): Int {
        Log.d("MasterpieceRepository", "Sending request: $request")
        return api.saveMasterpiece(request)
    }
}