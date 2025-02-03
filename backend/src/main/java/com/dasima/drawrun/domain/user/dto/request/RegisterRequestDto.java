package com.dasima.drawrun.domain.user.dto.request;

import com.dasima.drawrun.domain.user.entity.User;
import lombok.Data;

@Data
public class RegisterRequestDto {
  private String userId;
  private String email;
  private String password;
  private String userName;
  private String nickname;

  public User toUserEntity() {
    return User
        .builder()
        .id(userId)
        .userEmail(email)
        .userName(userName)
        .userNickname(nickname)
        .build();
  }

}
