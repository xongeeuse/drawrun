package com.dasima.drawrun.domain.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class BadgeInfoDto {

    private int badgeId;
    private String badgeName;
    private String badgeDes;
    private String badgeImg;

}
