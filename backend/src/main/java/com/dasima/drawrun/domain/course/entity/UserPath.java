package com.dasima.drawrun.domain.course.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserPath {
    private int userPathId;
    private int userId;
    private double distance;
    private String pathId; // mongodb id
    private String pathImgUrl;
    private String name;
    private String address;
    private LocalDateTime createDate;
}