package com.dasima.drawrun.domain.course.dto.response;

import com.dasima.drawrun.domain.course.vo.GeoPoint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CourseResponse {
    private List<GeoPoint> path;
    private String location;
    private int userPathId;
    private double distance;
}
