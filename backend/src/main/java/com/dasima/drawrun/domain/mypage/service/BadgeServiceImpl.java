package com.dasima.drawrun.domain.mypage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BadgeServiceImpl implements BadgeService {

  @Autowired
  BadgeService badgeService;
  @Autowired
  BadgeInventoryRepository badgeInventoryRepo;

  @Override
  public List<UserBadgeDto> grant(int userId) {
    return null;
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
