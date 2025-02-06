package com.dasima.drawrun.domain.course.dto;

import com.dasima.drawrun.domain.map.vo.GeoPoint;
import lombok.Data;

import java.util.List;

@Data
public class CourseSaveRequest {
    private List<GeoPoint> path; // 경로
    private String pathImgUrl;
    private String name;
    private double distance;
}
