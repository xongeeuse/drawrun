package com.dasima.drawrun.domain.result.dto.request;

import lombok.Data;

@Data
public class CourseResultSaveRequest {

  private String runImgUrl;
  private Double distanceKm;
  private Long timeS;
  private Long paceS;
  private Integer state;
  private Integer heartbeat;
  private Integer cadence;

}
