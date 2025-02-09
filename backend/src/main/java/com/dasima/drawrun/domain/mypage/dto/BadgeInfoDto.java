package com.dasima.drawrun.domain.mypage.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BadgeInfoDto {

    private int badgeId;
    private String badgeName;
    private String badgeDes;
    private String badgeImg;

    @JsonCreator
    public BadgeInfoDto(
        @JsonProperty("badgeId") Integer badgeId,
        @JsonProperty("badgeName") String badgeName,
        @JsonProperty("badgeDes") String badgeDes,
        @JsonProperty("badgeImg") String badgeImg) {
        this.badgeId = badgeId;
        this.badgeName = badgeName;
        this.badgeDes = badgeDes;
        this.badgeImg = badgeImg;
    }

}
