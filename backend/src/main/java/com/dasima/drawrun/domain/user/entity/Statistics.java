package com.dasima.drawrun.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "statistics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Statistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "statistics_id")
    private Integer statisticsId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "accumulated_distance")
    private Double accumulatedDistance;

    @Column(name = "accumulated_time")
    private Long accumulatedTime;

    @Column(name = "accumulated_heartbeat")
    private Long accumulatedHeartbeat;

    @Column(name = "accumulated_pace")
    private Long accumulatedPace;

    @Column(name = "accumulated_cadence")
    private Long accumulatedCadence;

    @Column(name = "average_heartbeat")
    private Double averageHeartbeat;

    @Column(name = "average_pace")
    private Double averagePace;

    @Column(name = "average_cadence")
    private Double averageCadence;

    @Column(name = "longest_streak")
    private Integer longestStreak;

    @Column(name = "current_streak")
    private Integer currentStreak;

    @Column(name = "latest_run")
    private LocalDateTime latestRun;
}
