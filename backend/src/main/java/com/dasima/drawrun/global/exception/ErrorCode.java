package com.dasima.drawrun.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
  DUPLICATE_MEMBER_ID(2001, "이미 사용중인 아이디입니다"),
  DUPLICATE_MEMBER_EMAIL(2002, "이미 사용중인 이메일입니다"),
  DUPLICATE_MEMBER_NICKNAME(2003, "이미 사용중인 닉네임입니다");


  private final int code;
  private final String defaultMessage;
}
