package com.dasima.drawrun.domain.result.dto.request;

import lombok.Data;

@Data
public class CourseResultSaveRequest {

  private Integer pathId;
  private String runImgUrl;
  private Double distance_km;
  private Long time_s;
  private Long pace_s;
  private Integer state;
  private Integer heartbeat;
  private Integer cadence;

}
