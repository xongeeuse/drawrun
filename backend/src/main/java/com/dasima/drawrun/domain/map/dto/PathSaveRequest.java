package com.dasima.drawrun.domain.map.dto;

import com.dasima.drawrun.domain.map.vo.GeoPoint;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.List;

@Data
public class PathSaveRequest {
    private List<GeoPoint> path; // 경로
    private int userId; // user pk
}
