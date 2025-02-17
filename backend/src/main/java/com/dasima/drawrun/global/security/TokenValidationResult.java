package com.dasima.drawrun.global.security;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class TokenValidationResult {

  private TokenStatus tokenStatus;
  private TokenType tokenType;
  private String tokenId; // tokenId
  private Claims claims; // 파싱된 토큰

  public String getEmail() {
    if(claims == null) {
      throw new IllegalArgumentException("Claim value is null");
    }
    return claims.getSubject();
  }

  public boolean isValid() {
    return TokenStatus.TOKEN_VALID == this.tokenStatus;
  }

}
