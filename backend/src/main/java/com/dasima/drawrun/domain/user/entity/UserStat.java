package com.dasima.drawrun.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_stat")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class UserStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stat_id")
    private int statId;
    private Float distanceKm;
    private Long timeS;
    private Long paceS;
    private Float calorie;
    private Integer state;
    @CreationTimestamp
    @Column(name = "date")
    private LocalDateTime date;
    private Integer heartbeat;
    private String runImgUrl;
    private Integer cadence;
    private Integer userId;
    private Integer userPathId;
}
