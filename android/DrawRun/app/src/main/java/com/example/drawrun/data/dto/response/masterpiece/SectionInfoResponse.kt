package com.example.drawrun.data.dto.response.masterpiece

typealias SectionInfoResponse = List<SectionInfo>

data class SectionInfo(
    val path: List<Coordinate>,
    val masterpieceSegId: Int,
    val address: String,
    val nickname: String
)

data class Coordinate(
    val latitude: Double,
    val longitude: Double
)
