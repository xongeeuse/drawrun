package com.dasima.drawrun.domain.mypage.service;

import com.dasima.drawrun.domain.mypage.dto.BadgeInfoDto;
import com.dasima.drawrun.domain.mypage.dto.UserBadgeDto;
import com.dasima.drawrun.domain.mypage.entity.BadgeInventory;
import com.dasima.drawrun.domain.mypage.repository.BadgeInventoryRepository;
import com.dasima.drawrun.domain.user.entity.Statistics;
import com.dasima.drawrun.domain.user.entity.UserStat;
import com.dasima.drawrun.domain.user.repository.StatisticsRepository;
import com.dasima.drawrun.domain.user.repository.UserRepository;
import com.dasima.drawrun.domain.user.repository.UserStatRepository;
import com.dasima.drawrun.global.config.BadgeConfig;
import java.time.LocalDateTime;
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
  @Autowired
  private UserStatRepository userStatRepository;
  @Autowired
  private StatisticsRepository statisticsRepository;

  @Override
  public List<UserBadgeDto> grant(int userId) {
    Statistics statistics = statisticsRepository.findStatisticsByUserId(userId).orElseGet(null);
    UserStat lastStat = userStatRepository.findFirstByUserIdOrderByDateDesc(userId).orElseGet(null);

    if(statistics == null || lastStat == null) {
      return null;
    }

    List<UserBadgeDto> userBadgeDtos = new ArrayList<>();
    List<BadgeInventory> currentBadge = badgeInventoryRepo.findBadgeInventoryByUserId(userId);
    int[] arr = new int[12];
    for(BadgeInventory badgeInventory : currentBadge) {
      arr[badgeInventory.getBadgeId()] = 1;
    }

    Map<Integer, BadgeInfoDto> badgeMap = badgeConfig.getBadgeInfoMap();
    LocalDateTime curTime = LocalDateTime.now();

    // 누적 거리
    if(arr[1] == 0) {
      if(statistics.getAccumulatedDistance() >= 1) {
        BadgeInventory badgeInventory = BadgeInventory.builder()
                .userId(userId)
                .badgeId(1)
                .collectedDate(curTime)
                .build();

        badgeInventoryRepo.save(badgeInventory);

        UserBadgeDto temp = UserBadgeDto.fromEntity(badgeInventory, badgeMap.get(badgeInventory.getBadgeId()));
        userBadgeDtos.add(temp);
      }
    }
    if(arr[2] == 0) {
      if(statistics.getAccumulatedDistance() >= 10) {
        BadgeInventory badgeInventory = BadgeInventory.builder()
            .userId(userId)
            .badgeId(2)
            .collectedDate(curTime)
            .build();

        badgeInventoryRepo.save(badgeInventory);

        UserBadgeDto temp = UserBadgeDto.fromEntity(badgeInventory, badgeMap.get(badgeInventory.getBadgeId()));
        userBadgeDtos.add(temp);
      }
    }
    if(arr[3] == 0) {
      if(statistics.getAccumulatedDistance() >= 42.195) {
        BadgeInventory badgeInventory = BadgeInventory.builder()
            .userId(userId)
            .badgeId(3)
            .collectedDate(curTime)
            .build();

        badgeInventoryRepo.save(badgeInventory);

        UserBadgeDto temp = UserBadgeDto.fromEntity(badgeInventory, badgeMap.get(badgeInventory.getBadgeId()));
        userBadgeDtos.add(temp);
      }
    }
    if(arr[4] == 0) {
      if(statistics.getAccumulatedDistance() >= 100) {
        BadgeInventory badgeInventory = BadgeInventory.builder()
            .userId(userId)
            .badgeId(4)
            .collectedDate(curTime)
            .build();

        badgeInventoryRepo.save(badgeInventory);

        UserBadgeDto temp = UserBadgeDto.fromEntity(badgeInventory, badgeMap.get(badgeInventory.getBadgeId()));
        userBadgeDtos.add(temp);
      }
    }
    if(arr[5] == 0) {
      if(statistics.getAccumulatedDistance() >= 500) {
        BadgeInventory badgeInventory = BadgeInventory.builder()
            .userId(userId)
            .badgeId(5)
            .collectedDate(curTime)
            .build();

        badgeInventoryRepo.save(badgeInventory);

        UserBadgeDto temp = UserBadgeDto.fromEntity(badgeInventory, badgeMap.get(badgeInventory.getBadgeId()));
        userBadgeDtos.add(temp);
      }
    }

    // 거리
    if(arr[6] == 0) {
      if(lastStat.getDistanceKm() >= 10) {
        BadgeInventory badgeInventory = BadgeInventory.builder()
            .userId(userId)
            .badgeId(6)
            .collectedDate(curTime)
            .build();

        badgeInventoryRepo.save(badgeInventory);

        UserBadgeDto temp = UserBadgeDto.fromEntity(badgeInventory, badgeMap.get(badgeInventory.getBadgeId()));
        userBadgeDtos.add(temp);
      }
    }
    if(arr[7] == 0) {
      if(lastStat.getDistanceKm() >= 20) {
        BadgeInventory badgeInventory = BadgeInventory.builder()
            .userId(userId)
            .badgeId(7)
            .collectedDate(curTime)
            .build();

        badgeInventoryRepo.save(badgeInventory);

        UserBadgeDto temp = UserBadgeDto.fromEntity(badgeInventory, badgeMap.get(badgeInventory.getBadgeId()));
        userBadgeDtos.add(temp);
      }
    }
    if(arr[8] == 0) {
      if(lastStat.getDistanceKm() >= 50) {
        BadgeInventory badgeInventory = BadgeInventory.builder()
            .userId(userId)
            .badgeId(8)
            .collectedDate(curTime)
            .build();

        badgeInventoryRepo.save(badgeInventory);

        UserBadgeDto temp = UserBadgeDto.fromEntity(badgeInventory, badgeMap.get(badgeInventory.getBadgeId()));
        userBadgeDtos.add(temp);
      }
    }

    // 스트릭
    if(arr[9] == 0) {
      if(statistics.getCurrentStreak() == 3) {
        BadgeInventory badgeInventory = BadgeInventory.builder()
            .userId(userId)
            .badgeId(9)
            .collectedDate(curTime)
            .build();

        badgeInventoryRepo.save(badgeInventory);

        UserBadgeDto temp = UserBadgeDto.fromEntity(badgeInventory, badgeMap.get(badgeInventory.getBadgeId()));
        userBadgeDtos.add(temp);
      }
    }

    if(arr[11] == 0) {
      if(statistics.getCurrentStreak() == 30) {
        BadgeInventory badgeInventory = BadgeInventory.builder()
            .userId(userId)
            .badgeId(11)
            .collectedDate(curTime)
            .build();

        badgeInventoryRepo.save(badgeInventory);

        UserBadgeDto temp = UserBadgeDto.fromEntity(badgeInventory, badgeMap.get(badgeInventory.getBadgeId()));
        userBadgeDtos.add(temp);
      }
    }

    return userBadgeDtos;
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
