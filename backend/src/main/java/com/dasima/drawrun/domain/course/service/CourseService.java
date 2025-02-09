package com.dasima.drawrun.domain.course.service;

import com.dasima.drawrun.domain.course.dto.request.CourseResultSaveRequest;

public interface CourseService {
  public int courseResultSave(CourseResultSaveRequest resultDto);
}
