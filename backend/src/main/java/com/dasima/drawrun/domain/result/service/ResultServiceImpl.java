package com.dasima.drawrun.domain.result.service;

import com.dasima.drawrun.domain.result.dto.request.CourseResultSaveRequest;
import com.dasima.drawrun.domain.result.entity.CourseResult;
import com.dasima.drawrun.domain.result.repository.ResultRepository;
import com.dasima.drawrun.global.exception.CustomException;
import com.dasima.drawrun.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResultServiceImpl implements ResultService {

  private final ResultRepository courseRepository;

  @Override
  public int courseResultSave(int UserPK, CourseResultSaveRequest resultDto) {
    new CourseResult();

    try {
      courseRepository.save(CourseResult
              .builder()
              .distanceKm(resultDto.getDistanceKm())
              .timeS(resultDto.getTimeS())
              .paceS(resultDto.getPaceS())
              .state(resultDto.getState())
              .heartbeat(resultDto.getHeartbeat())
              .runImgUrl(resultDto.getRunImgUrl())
              .cadence(resultDto.getCadence())
              .userId(UserPK)
              .build()
      );
    } catch (Exception e) {
      log.info(e.getMessage());
      throw new CustomException(ErrorCode.RESULT_SAVE_ERROR);
    }

    return 1;
  }

}
