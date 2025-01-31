package com.dasima.drawrun.domain.user.controller;

import com.dasima.drawrun.domain.user.dto.request.LoginRequestDto;
import com.dasima.drawrun.domain.user.dto.request.RegisterRequestDto;
import com.dasima.drawrun.domain.user.service.UserService;
import com.dasima.drawrun.global.common.ApiResponseJson;
import com.dasima.drawrun.global.exception.CustomException;
import com.dasima.drawrun.global.security.dto.response.TokenResponseDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class UserController {

  @Autowired
  UserService userService;

  @PostMapping("/register")
  public ResponseEntity<ApiResponseJson> register(@RequestBody RegisterRequestDto registerRequestDto) {
    try{
      Object responseData = userService.register(registerRequestDto);
      return ResponseEntity.ok(
        new ApiResponseJson(true, 200, "회원가입에 성공했습니다.", responseData)
      );
    }
    catch(CustomException e) {
      return ResponseEntity.ok(
          new ApiResponseJson(false, e.getErrorCode().getCode(), e.getMessage(), null));
    }
    catch(Exception e) {
      return ResponseEntity.status(500)
          .body(new ApiResponseJson(false, 500, "서버 에러가 발생했습니다.", null));
    }
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponseJson> login(@RequestBody LoginRequestDto loginRequestDto,
      HttpServletResponse response) {
    try {
      TokenResponseDto tokenResponseDto = userService.login(loginRequestDto.getUserId(),
          loginRequestDto.getPassword());

      // refresh 토큰은 쿠키에 저장
      Cookie cookie = new Cookie("refresh", tokenResponseDto.getRefreshTokenInfoResponse());
      // cookie 설정
      cookie.setMaxAge(604800);
      cookie.setHttpOnly(true);
      response.addCookie(cookie);

      return ResponseEntity.ok(
          new ApiResponseJson(true, 200, "로그인에 성공했습니다.",
              tokenResponseDto.getAccessTokenInfoResponse())
      );
    } catch (CustomException e) {
      return ResponseEntity.ok(
          new ApiResponseJson(false, e.getErrorCode().getCode(), e.getMessage(), null)
      );
    } catch (Exception e) {
      return ResponseEntity.status(500)
          .body(new ApiResponseJson(false, 500, "서버 에러가 발생했습니다.", null));
    }
  }

}
