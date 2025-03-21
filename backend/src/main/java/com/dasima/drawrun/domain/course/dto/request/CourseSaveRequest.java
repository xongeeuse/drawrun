package com.dasima.drawrun.domain.course.dto.request;

import com.dasima.drawrun.domain.course.vo.GeoPoint;
import lombok.Data;

import java.util.List;

@Data
public class CourseSaveRequest {
    private List<GeoPoint> path; // 경로
    private String pathImgUrl;
    private String name;
    private double distance;
}
