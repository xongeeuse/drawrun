package com.dasima.drawrun.domain.mypage.service;

import com.dasima.drawrun.domain.mypage.dto.BadgeInfoDto;
import com.dasima.drawrun.domain.mypage.dto.UserBadgeDto;
import com.dasima.drawrun.domain.mypage.entity.BadgeInventory;
import com.dasima.drawrun.domain.mypage.repository.BadgeInventoryRepository;
import com.dasima.drawrun.global.config.BadgeConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BadgeServiceImpl implements BadgeService {

  @Autowired
  BadgeConfig badgeConfig;

  @Autowired
  BadgeInventoryRepository badgeInventoryRepo;

  @Override
  public List<UserBadgeDto> grant(int userId) {
    return null;
  }

  @Override
  public List<UserBadgeDto> check(int userId) {
    List<BadgeInventory> badgeInventoryList = badgeInventoryRepo.findBadgeInventoryByUserId(userId);
    Map<Integer, BadgeInfoDto> badgeMap = badgeConfig.getBadgeInfoMap();

    List<UserBadgeDto> userBadgeDtoList = new ArrayList<>();
    for (BadgeInventory badgeInventory : badgeInventoryList) {
      UserBadgeDto temp = UserBadgeDto.fromEntity(badgeInventory, badgeMap.get(badgeInventory.getBadgeId()));
      userBadgeDtoList.add(temp);
    }
    return userBadgeDtoList;
  }

}
