package com.dasima.drawrun.domain.user.service;

import com.dasima.drawrun.domain.course.entity.UserPath;
import com.dasima.drawrun.domain.course.mapper.CourseMapper;
import com.dasima.drawrun.domain.user.dto.HistoryDto;
import com.dasima.drawrun.domain.user.dto.response.UserArtsResponse;
import com.dasima.drawrun.domain.user.dto.response.UserHistoryResponse;
import com.dasima.drawrun.domain.user.entity.User;
import com.dasima.drawrun.domain.user.entity.UserStat;
import com.dasima.drawrun.domain.user.repository.UserRepository;
import com.dasima.drawrun.domain.user.repository.UserStatRepository;
import com.dasima.drawrun.global.exception.CustomException;
import com.dasima.drawrun.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserStatRepository userStatRepository;

    @Autowired
    private UserRepository userRepository;
  @Autowired
  private CourseMapper courseMapper;

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

}
