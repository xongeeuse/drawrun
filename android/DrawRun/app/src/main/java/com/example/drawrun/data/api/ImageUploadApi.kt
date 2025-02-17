package com.example.drawrun.data.api

import com.example.drawrun.data.dto.response.image.ImageUploadResponse
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ImageUploadApi {
    @Multipart
    @POST("file/upload")
    suspend fun uploadImage(@Part file: MultipartBody.Part): ImageUploadResponse
}

data class ImageUploadResponse(
    val url: String
)
