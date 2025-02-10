package com.dasima.drawrun.domain.result.service;

import com.dasima.drawrun.domain.result.dto.request.CourseResultSaveRequest;

public interface ResultService {
  public int courseResultSave(int UserPK, CourseResultSaveRequest resultDto);
}
