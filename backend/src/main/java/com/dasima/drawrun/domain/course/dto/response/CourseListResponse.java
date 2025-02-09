package com.dasima.drawrun.domain.course.dto.response;

import lombok.Data;

@Data
public class CourseListResponse {
    private String username;
    private String courseName;
    private String area;
    int isBookmark;
    double distance;
    private String courseImgUrl;
}
