package com.dasima.drawrun.domain.masterpiece.dto.request;

import com.dasima.drawrun.domain.course.vo.GeoPoint;
import lombok.Data;

import java.util.List;

@Data
public class MasterpieceSaveRequest {
    private int userPathId;
    private List<List<GeoPoint>> paths;
    private int restrictCount;
}
