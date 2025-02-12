package com.dasima.drawrun.domain.user.service;

import com.dasima.drawrun.domain.course.entity.UserPath;
import com.dasima.drawrun.domain.course.mapper.CourseMapper;
import com.dasima.drawrun.domain.user.dto.HistoryDto;
import com.dasima.drawrun.domain.user.dto.response.UserHistoryResponse;
import com.dasima.drawrun.domain.user.entity.User;
import com.dasima.drawrun.domain.user.entity.UserStat;
import com.dasima.drawrun.domain.user.repository.UserRepository;
import com.dasima.drawrun.domain.user.repository.UserStatRepository;
import com.dasima.drawrun.global.exception.CustomException;
import com.dasima.drawrun.global.exception.ErrorCode;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        List<UserStat> list = userStatRepository.findByUserId(userId);

        List<HistoryDto> historyDtoList = list.stream().map(userStat -> {
            UserPath path = courseMapper.search(userStat.getUserPathId());

            return HistoryDto.builder()
                .userPathId(userStat.getUserPathId())
                .pathId(path.getPathId())
                .pathImgUrl(userStat.getRunImgUrl())
                .name(path.getName())
                .createDate(userStat.getDate())
                .distance(Double.valueOf(userStat.getDistanceKm()))
                .address(path.getAddress())
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


}
