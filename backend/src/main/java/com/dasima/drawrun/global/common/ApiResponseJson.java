package com.dasima.drawrun.global.common;

import com.dasima.drawrun.global.exception.ErrorCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

// custom 응답 객체
@Getter
@ToString
@NoArgsConstructor
public class ApiResponseJson {

  public Boolean isSuccess;
  public String message;
  public int code;
  public Object data;

  public ApiResponseJson(Boolean isSuccess, int code, String message, Object data) {
    this.isSuccess = isSuccess;
    this.message = message;
    this.code = code;
    this.data = data;
  }


  public ApiResponseJson(ErrorCode errorCode, Object data) {
    this.isSuccess = false;
    this.message = errorCode.getDefaultMessage();
    this.code = errorCode.getCode();
    this.data = data;
  }

}
