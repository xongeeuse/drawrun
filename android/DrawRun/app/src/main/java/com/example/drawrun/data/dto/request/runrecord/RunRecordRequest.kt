package com.example.drawrun.data.dto.request.runrecord

data class RunRecordRequest(
    val pathId: Int,           // 코스 ID
    val endImgUrl: String?,    // 러닝 종료 스냅샷 이미지 URL
    val distance_km: Double,   // 총 이동 거리 (km)
    val time_s: Int,           // 총 소요 시간 (초)
    val pace_s: Double,        // 평균 페이스 (초/km)
    val state: Int,            // 러닝 상태 (예: 1=완료)
    val heartbeat: Int,        // 평균 심박수 (BPM)
    val cadence: Int?          // 케이던스 (보폭)
)