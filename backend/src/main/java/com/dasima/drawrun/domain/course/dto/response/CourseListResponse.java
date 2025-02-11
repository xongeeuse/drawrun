package com.dasima.drawrun.domain.course.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseListResponse {
    private int courseId;
    private int userPK;
    private String userNickname;
    private String profileImgUrl;
    private String courseName;
    private String location;
    private Boolean isBookmark;
    private LocalDateTime createdAt;
    private double distance;
    private String courseImgUrl;
    private int bookmarkCount;
}
