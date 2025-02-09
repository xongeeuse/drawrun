package com.dasima.drawrun.domain.course.dto.request;

import lombok.Data;

@Data
public class CourseResultSaveRequest {

  private String userId;
  private Integer pathId;
  private String startImgUrl;
  private String endImgUrl;
  private double distance_km;
  private Long time_s;
  private Long pace_s;
  private Integer calorie;
  private Integer state;
  private Integer heartbeat;
  private Integer cadence;

}
