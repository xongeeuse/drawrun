package com.dasima.drawrun.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
  DUPLICATE_MEMBER_ID(2001, "이미 사용중인 아이디입니다"),
  DUPLICATE_MEMBER_EMAIL(2002, "이미 사용중인 이메일입니다"),
  DUPLICATE_MEMBER_NICKNAME(2003, "이미 사용중인 닉네임입니다"),

  NOT_EXIST_MEMBER_ID(2004, "존재하지 않는 아이디입니다."),
  INCORRECT_MEMBER_PASSWORD(2005, "비밀번호가 일치하지 않습니다."),

  INVALID_REFRESH_TOKEN(2006, "유효하지 않는 토큰입니다."),
  NOT_EXIST_MEMBER_EMAIL(2007, "존재하지 않는 이메일입니다."),

  S3_ERROR(3001, "S3 처리 과정에 오류가 생겼습니다."),
  INVALID_STORAGE_URL(3002, "파일이 존재하지 않습니다.");

  private final int code;
  private final String defaultMessage;
}
