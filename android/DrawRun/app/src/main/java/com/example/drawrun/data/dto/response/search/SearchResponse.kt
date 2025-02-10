package com.example.drawrun.data.dto.response.search

typealias SearchResponse = List<CourseData>

//data class SearchResponse(
//    val isSuccess: Boolean,
//    val message: String,
//    val code: Int,
//    val data: List<CourseData>,
//)

// 변수명 다시 체크
data class CourseData(
    val courseId: String,       // 코스 pk
    val courseName: String,     // 코스 이름
    val distance: Double,       // 코스 총 거리
    val location: String?,       // 코스 지역 정보
    val courseImgUrl: String,   // 코스 이미지 주소
    val createdAt: String,      // 코스 생성일
    val userPK: Int,            // 코스 등록 유저의 pk
    val userNickname: String,   // 코스 등록 유저의 닉네임
    val profileImgUrl: String?,  // 코스 등록 유저의 프로필 이미지 주소
    val bookmarkCount: Int,     // 해당 코스의 총 북마크 수
    val isBookmark: Boolean,  // 유저의 해당 코스 북마크 여부 확인
)