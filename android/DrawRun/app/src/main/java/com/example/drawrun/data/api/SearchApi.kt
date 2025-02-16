package com.example.drawrun.data.api

import com.example.drawrun.data.dto.response.search.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {
    @GET("course/list")
    suspend fun getTopCourses(): Response<SearchResponse>

    @GET("course/list")
    suspend fun searchCoursesByKeyword(@Query("keyword") keyword: String): Response<SearchResponse>

    @GET("course/list")
    suspend fun searchCoursesByLocation(@Query("area") area: String): Response<SearchResponse>
}