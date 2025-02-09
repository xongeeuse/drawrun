package com.dasima.drawrun.domain.course.service;

import com.dasima.drawrun.domain.course.dto.request.CourseResultSaveRequest;

public interface CourseService {
  public int courseResultSave(int UserPK, CourseResultSaveRequest resultDto);
}
