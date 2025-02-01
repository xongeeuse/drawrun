package com.dasima.drawrun.global.security.dto.response;

import java.util.Date;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString(exclude= {"accessToken"})
public class AccessTokenInfoResponseDto {
  private String accessToken; // access token

  private Date accessTokenExpireTime; // 토큰의 만료날
  private String email; // 사용자 이메일
  private String tokenId;
}
