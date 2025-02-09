package com.dasima.drawrun.domain.course.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Bookmark {
    private int bookmarkId;
    private int userId;
    private int userPathId;
}
