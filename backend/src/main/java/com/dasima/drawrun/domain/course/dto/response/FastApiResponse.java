package com.dasima.drawrun.domain.course.dto.response;

import com.dasima.drawrun.domain.course.dto.Coordinate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FastApiResponse {

  private List<Coordinate> path;

}
