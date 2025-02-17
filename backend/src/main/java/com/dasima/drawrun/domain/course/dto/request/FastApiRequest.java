package com.dasima.drawrun.domain.course.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FastApiRequest {

  private double lon;
  private double lat;
  private String mapDataUrl;
  private String imgUrl;
  
}
