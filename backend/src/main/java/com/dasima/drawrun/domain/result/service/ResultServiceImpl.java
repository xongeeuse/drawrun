package com.dasima.drawrun.domain.result.service;

import com.dasima.drawrun.domain.result.dto.request.CourseResultSaveRequest;
import com.dasima.drawrun.domain.result.entity.CourseResult;
import com.dasima.drawrun.domain.result.repository.ResultRepository;
import com.dasima.drawrun.domain.user.entity.Statistics;
import com.dasima.drawrun.domain.user.repository.StatisticsRepository;
import com.dasima.drawrun.global.exception.CustomException;
import com.dasima.drawrun.global.exception.ErrorCode;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ResultServiceImpl implements ResultService {

  private final ResultRepository courseRepository;
  private final StatisticsRepository statisticsRepository;

  @Override
  public int courseResultSave(int UserPK, CourseResultSaveRequest resultDto) {
    // 기존 통계 조회, 없으면 새로 생성 (각 필드는 null 대신 기본값 0 사용)
    Statistics stat = statisticsRepository.findStatisticsByUserId(UserPK)
        .orElseGet(() -> statisticsRepository.save(
            Statistics.builder()
                .userId(UserPK)
                .accumulatedDistance(0.0)
                .accumulatedTime(0L)
                .accumulatedHeartbeat(0L)
                .accumulatedPace(0L)
                .averageHeartbeat(0.0)
                .averagePace(0.0)
                .averageCadence(0.0)
                .longestStreak(0)
                .currentStreak(0)
                .latestRun(null)
                .build()
        ));

    // 1. 새 달리기 기록 저장 (CourseResult 저장 로직은 기존과 동일)
    try {
      courseRepository.save(
          CourseResult.builder()
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

    // 2. 통계 업데이트
    // 누적 거리, 시간, 페이스, 캐이던스는 항상 값이 들어온다고 가정
    stat.setAccumulatedDistance(((stat.getAccumulatedDistance() != null) ? stat.getAccumulatedDistance() : 0.0) + resultDto.getDistanceKm());
    stat.setAccumulatedTime(((stat.getAccumulatedTime() != null) ? stat.getAccumulatedTime() : 0L) + resultDto.getTimeS());
    stat.setAccumulatedPace(((stat.getAccumulatedPace() != null) ? stat.getAccumulatedPace() : 0L) + resultDto.getPaceS());

    if (stat.getAccumulatedCadence() == null) {
      stat.setAccumulatedCadence(0L);
    }

    // Update accumulated cadence and run count
    long newAccumulatedCadence = stat.getAccumulatedCadence() + resultDto.getCadence();
    int newRunCount = ((stat.getAverageCadence() != null) ? (int) (stat.getAccumulatedCadence() / stat.getAverageCadence()) : 0) + 1;

    stat.setAccumulatedCadence(newAccumulatedCadence);

    double newAverageCadence = (double) newAccumulatedCadence / newRunCount;
    stat.setAverageCadence(newAverageCadence);

    // 평균 심박수: heartbeat가 null이 아닐 경우만 업데이트.
    if (resultDto.getHeartbeat() != null) {
      // 이전 누적 심박수 가져오기, null인 경우 0으로 초기화
      long prevAccumHB = stat.getAccumulatedHeartbeat() != null ? stat.getAccumulatedHeartbeat() : 0L;
      // 새로운 누적 심박수 계산
      long newAccumHB = prevAccumHB + resultDto.getHeartbeat();

      // runCount 가져오기, null인 경우 0으로 초기화
      int runCount =
          stat.getAccumulatedHeartbeat() != null ? (int) (stat.getAccumulatedHeartbeat()
              / stat.getAverageHeartbeat()) : 0;
      // runCount 증가
      runCount++;
      // runCount 업데이트


      // 누적 심박수 업데이트
      stat.setAccumulatedHeartbeat(newAccumHB);
      
      // 새로운 평균 심박수 계산 및 업데이트
      double newAvgHB = (double) newAccumHB / runCount;
      stat.setAverageHeartbeat(newAvgHB);
    }

    double newAvgPace = (double) stat.getAccumulatedPace() / newRunCount;
    stat.setAveragePace(newAvgPace);

    // 3. 스트릭 업데이트
    // runDate: 현재 날짜 사용
    java.time.LocalDate newRunDate = java.time.LocalDate.now();
    if (stat.getLatestRun() != null) {
      java.time.LocalDate previousRunDate = stat.getLatestRun();
      // 만약 이전 기록이 바로 전 날이 아니라면 스트릭은 1로 초기화
      if (!previousRunDate.plusDays(1).equals(newRunDate)) {
        stat.setCurrentStreak(1);
      } else {
        stat.setCurrentStreak(stat.getCurrentStreak() + 1);
      }
    } else {
      stat.setCurrentStreak(1);
    }

    // 최장 스트릭 업데이트
    int maxStreak = (stat.getLongestStreak() != null) ? stat.getLongestStreak() : 0;
    if (stat.getCurrentStreak() > maxStreak) {
      stat.setLongestStreak(stat.getCurrentStreak());
    }
    // 마지막 달리기 날짜 업데이트
    stat.setLatestRun(newRunDate);

    // 4. 업데이트된 통계 저장
    statisticsRepository.save(stat);

    return 1;
  }

}
