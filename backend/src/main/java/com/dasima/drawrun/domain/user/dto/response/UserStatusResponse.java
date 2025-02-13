package com.dasima.drawrun.domain.user.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserStatusResponse {
    private double totalDistanceKm; // 누적 거리
    private long totalTimeS;        // 누적 시간
    private double averageHeartbeat; // 평균 심박수
    private double averagePaceS;     // 평균 페이스
    private double averageCadence;   // 평균 캐이던스
    private int longestStreak;       // 최장 스트릭
    private int currentStreak;       // 현재 스트릭
}
