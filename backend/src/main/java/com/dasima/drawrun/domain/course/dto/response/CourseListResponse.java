package com.dasima.drawrun.domain.course.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseListResponse {
    private String username;
    private String courseName;
    private String area;
    Boolean isBookmark;
    double distance;
    private String courseImgUrl;
}
