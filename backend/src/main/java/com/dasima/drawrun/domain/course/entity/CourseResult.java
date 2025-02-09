package com.dasima.drawrun.domain.course.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

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
  private Float distanceKm;
  private Integer timeS;
  private Integer paceS;
  private Float calorie;
  private Integer state;
  @CreationTimestamp
  @Column(name = "date")
  private LocalDateTime date;
  private Integer heartbeat;
  private String runImgUrl;
  private Integer cadence;
  private Integer userPathId;

}
