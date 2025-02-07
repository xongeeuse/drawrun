package com.dasima.drawrun.domain.course.service;

import com.dasima.drawrun.domain.course.dto.CourseSaveRequest;

public interface CourseService {
    int save(CourseSaveRequest dto, int userId);
}
