package com.dasima.drawrun.global.security;

// 현재 토큰의 상태
public enum TokenStatus {
  TOKEN_VALID("정상 토큰입니다."),
  TOKEN_EXPIRED("토큰이 만료되었습니다."),
  TOKEN_IS_BLACKLIST("토큰이 폐기된 상태입니다."),
  TOKEN_WRONG_SIGNATURE("잘못된 토큰입니다."),
  TOKEN_HASH_NOT_SUPPORTED("지원하지 않는 형식의 토큰입니다."), // 서명 방식이 다른 경우나 지원하지 않을 때
  WRONG_AUTH_HEADER("[Bearer ]로 시작하는 토큰이 없습니다."), // Authroization: Bearer Token 이런 형식이 아닌 경우 오류를 발생시킨다.
  TOKEN_VALIDATION_TRY_FAILED("인증에 실패했습니다.");

  private final String message;

  TokenStatus(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
