package com.dasima.drawrun.domain.course.dto.request;

import com.dasima.drawrun.domain.course.entity.Bookmark;
import lombok.Data;

@Data
public class BookmarkCreateRequest {
    private int userPathId;
}
