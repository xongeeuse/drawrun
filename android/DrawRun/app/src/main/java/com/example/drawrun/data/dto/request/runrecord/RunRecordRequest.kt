package com.example.drawrun.data.dto.request.runrecord

data class RunRecordRequest(
    val runImgUrl: String?,    // 러닝 종료 스냅샷 이미지 URL
    val distanceKm: Double,    // 총 이동 거리 (km)
    val timeS: Int,            // 총 소요 시간 (초)
    val paceS: Int,            // 평균 페이스 (초/km)
    val state: Int = 1,        // 러닝 상태 (무조건 1)
    val heartbeat: Int,        // 평균 심박수 (BPM)
    val cadence: Int?          // 케이던스 (보폭) null 가능
)