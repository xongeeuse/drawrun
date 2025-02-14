package com.dasima.drawrun.domain.user.service;

import com.dasima.drawrun.domain.course.entity.UserPath;
import com.dasima.drawrun.domain.course.mapper.CourseMapper;
import com.dasima.drawrun.domain.user.dto.HistoryDto;
import com.dasima.drawrun.domain.user.dto.response.UserArtsResponse;
import com.dasima.drawrun.domain.user.dto.response.UserHistoryResponse;
import com.dasima.drawrun.domain.user.dto.response.UserStatusResponse;
import com.dasima.drawrun.domain.user.entity.Statistics;
import com.dasima.drawrun.domain.user.entity.User;
import com.dasima.drawrun.domain.user.entity.UserStat;
import com.dasima.drawrun.domain.user.repository.StatisticsRepository;
import com.dasima.drawrun.domain.user.repository.UserRepository;
import com.dasima.drawrun.domain.user.repository.UserStatRepository;
import com.dasima.drawrun.global.exception.CustomException;
import com.dasima.drawrun.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStatRepository userStatRepository;
    private final UserRepository userRepository;
    private final CourseMapper courseMapper;
    private final StatisticsRepository statisticsRepository;

    @Override
    public UserHistoryResponse getHistoryById(int userId) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(()-> new CustomException(ErrorCode.NOT_EXIST_MEMBER_ID));
        List<UserStat> list = userStatRepository.findByUserIdOrderByDateDesc(userId);

        List<HistoryDto> historyDtoList = list.stream().map(userStat -> {
            return HistoryDto.builder()
                    .pathImgUrl(userStat.getRunImgUrl())
                    .createDate(userStat.getDate())
                    .distance(userStat.getDistanceKm())
                    .time(userStat.getTimeS())
                    .pace(userStat.getPaceS())
                    .heartbeat(userStat.getHeartbeat())
                    .cadence(userStat.getCadence())
                    .build();
        }).collect(Collectors.toList());

      return UserHistoryResponse
                .builder()
                .userPK(userId)
                .nickname(user.getUserNickname())
                .profileImgUrl(user.getProfileImgUrl())
                .history(historyDtoList)
                .build();
    }

    @Override
    public UserArtsResponse getArtById(int userId) {
        List<UserPath> list = courseMapper.findByUserPK(userId);

        return UserArtsResponse
                .builder()
                .artList(list)
                .build();
    }

    @Override
    public UserStatusResponse getUserStatById(int userId) {
        Statistics stat = statisticsRepository.findStatisticsByUserId(userId)
                .orElseGet(() -> statisticsRepository.save(
                        Statistics.builder()
                                .userId(userId)
                                .build()
                ));


        return UserStatusResponse.builder()
                .totalDistanceKm(Optional.ofNullable(stat.getAccumulatedDistance()).orElse(0.0))
                .totalTimeS(Optional.ofNullable(stat.getAccumulatedTime()).orElse(0L))
                .averageHeartbeat(Optional.ofNullable(stat.getAverageHeartbeat()).orElse(0.0))
                .averagePaceS(Optional.ofNullable(stat.getAveragePace()).orElse(0.0))
                .averageCadence(Optional.ofNullable(stat.getAverageCadence()).orElse(0.0))
                .longestStreak(Optional.ofNullable(stat.getLongestStreak()).orElse(0))
                .currentStreak(Optional.ofNullable(stat.getCurrentStreak()).orElse(0))
                .build();

    }

    @Override
    public String getRegionById(int userId) {
        User user = userRepository.getUserByUserId(userId);

        return user.getRegion();
    }

    @Override
    public void setRegionById(int userId, String region) {
        User user = userRepository.getUserByUserId(userId);
        user.setRegion(region);

        userRepository.save(user);
    }

}
