package com.dasima.drawrun.domain.result.service;

import com.dasima.drawrun.domain.result.dto.request.CourseResultSaveRequest;
import com.dasima.drawrun.domain.result.entity.CourseResult;
import com.dasima.drawrun.domain.result.repository.ResultRepository;
import com.dasima.drawrun.global.exception.CustomException;
import com.dasima.drawrun.global.exception.ErrorCode;
import org.springframework.stereotype.Service;

@Service
public class ResultServiceImpl implements ResultService {

  private ResultRepository courseRepository;

  @Override
  public int courseResultSave(int UserPK, CourseResultSaveRequest resultDto) {
    new CourseResult();

    try {
      courseRepository.save(CourseResult
          .builder()
          .distanceKm(resultDto.getDistance_km())
          .timeS(resultDto.getTime_s())
          .paceS(resultDto.getPace_s())
          .calorie(resultDto.getCalorie())
          .state(1)
          .heartbeat(resultDto.getHeartbeat())
          .runImgUrl(resultDto.getEndImgUrl())
          .cadence(resultDto.getCadence())
          .userId(UserPK)
          .userPathId(resultDto.getPathId())
          .build()
      );
    } catch (Exception e) {
      throw new CustomException(ErrorCode.RESULT_SAVE_ERROR);
    }

    return 1;
  }

}
