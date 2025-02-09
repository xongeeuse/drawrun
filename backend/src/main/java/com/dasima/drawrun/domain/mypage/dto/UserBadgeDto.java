package com.dasima.drawrun.domain.mypage.dto;

import com.dasima.drawrun.domain.mypage.entity.BadgeInventory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
public class UserBadgeDto {

    private int badgeId;
    private String badgeName;
    private String badgeDes;
    private String badgeImg;
    private LocalDateTime badgeTime;

    public static UserBadgeDto fromEntity(BadgeInventory badgeInventory, BadgeInfoDto badgeInfoDto) {
        return UserBadgeDto.builder()
                .badgeId(badgeInventory.getBadgeId())
                .badgeName(badgeInfoDto.getBadgeName())
                .badgeDes(badgeInfoDto.getBadgeDes())
                .badgeImg(badgeInfoDto.getBadgeImg())
                .badgeTime(badgeInventory.getCollectedDate())
                .build();
    }

}
