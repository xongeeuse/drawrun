package com.example.drawrun.data.dto.response.course

//data class CourseSaveResponse(
//    val isSuccess: Boolean,
//    val message: String,
//    val code: Int,
//    val data: String?,      // nullable로 지정
//)

// 응답 양식 변경
data class CourseSaveResponse(val courseId: Int)
