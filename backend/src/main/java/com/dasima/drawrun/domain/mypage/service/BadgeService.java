package com.dasima.drawrun.domain.mypage.service;

import com.dasima.drawrun.domain.mypage.dto.UserBadgeDto;

import java.util.List;

public interface BadgeService {

    List<UserBadgeDto> grant(int userId);

    List<UserBadgeDto> check(int userId);

}
