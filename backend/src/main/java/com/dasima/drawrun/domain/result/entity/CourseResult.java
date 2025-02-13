package com.dasima.drawrun.domain.result.entity;

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
public class CourseResult {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "stat_id")
  private Integer statId;
  private Double distanceKm;
  @Column(name = "time_s")
  private Long timeS;
  @Column(name = "pace_s")
  private Long paceS;
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
