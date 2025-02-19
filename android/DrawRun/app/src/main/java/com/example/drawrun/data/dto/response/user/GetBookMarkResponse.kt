package com.example.drawrun.data.dto.response.user

typealias GetBookMarkResponse = List<BookMarkData>

data class BookMarkData(
    val courseName: String,
    val pathImgUrl: String,
    val address: String,
    val userPathPk: Int
)