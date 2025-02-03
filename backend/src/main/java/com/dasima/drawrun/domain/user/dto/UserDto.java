package com.dasima.drawrun.domain.user.dto;

import com.dasima.drawrun.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserDto {

  private Integer userId;
  private String id;
  private String userEmail;
  private String userName;
  private String userNickname;
  private String profileImgUrl;
  private Integer badgeId;

  public static UserDto fromEntity(User user) {
    return UserDto.builder()
        .userId(user.getUserId())
        .id(user.getId())
        .userEmail(user.getUserEmail())
        .userName(user.getUserName())
        .userNickname(user.getUserNickname())
        .profileImgUrl(user.getProfileImgUrl())
        .badgeId(user.getBadgeId())
        .build();
  }

}
