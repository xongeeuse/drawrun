package com.dasima.drawrun.domain.course.service;

import com.dasima.drawrun.domain.course.dto.request.BookmarkCreateRequest;
import com.dasima.drawrun.domain.course.dto.request.CourseSaveRequest;

public interface CourseService {
    int save(CourseSaveRequest dto, int userId);
    public int bookmark(BookmarkCreateRequest dto, int userId);
}
