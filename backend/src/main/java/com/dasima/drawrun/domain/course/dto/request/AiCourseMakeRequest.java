package com.dasima.drawrun.domain.course.dto.request;

import lombok.Data;

@Data
public class AiCourseMakeRequest {
    private Double lat;
    private Double lon;
    private String paintUrl;
}
