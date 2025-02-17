package com.example.drawrun.data.dto.response.user

data class GetMyArtCustomResponse(
    val isSuccess: Boolean,
    val message: String,
    val code : Int,
    val data: ArtListData
)

data class ArtListData(
    val artList: List<ArtData>
)

data class ArtData(
    val userPathId: Int,
    val userId: Int,
    val distance: Double,
    val pathId: String,
    val pathImgUrl: String,
    val name: String,
    val address: String,
    val createDate: String,
    val bookmarkCount: Int
)