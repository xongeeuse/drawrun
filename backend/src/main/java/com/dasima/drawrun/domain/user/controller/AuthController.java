package com.dasima.drawrun.domain.user.controller;

import com.dasima.drawrun.domain.user.dto.request.LoginRequestDto;
import com.dasima.drawrun.domain.user.dto.request.RegisterRequestDto;
import com.dasima.drawrun.domain.user.service.AuthService;
import com.dasima.drawrun.global.common.ApiResponseJson;
import com.dasima.drawrun.global.exception.CustomException;
import com.dasima.drawrun.global.security.dto.response.TokenResponseDto;
import com.dasima.drawrun.global.security.filter.JwtFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  @Autowired
  AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<ApiResponseJson> register(@RequestBody RegisterRequestDto registerRequestDto) {
    try{
      Object responseData = authService.register(registerRequestDto);
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
      TokenResponseDto tokenResponseDto = authService.login(loginRequestDto.getUserId(),
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

  @PostMapping("/reissue")
  public ResponseEntity<ApiResponseJson> reissue(@CookieValue(value = "refresh") String refresh, HttpServletResponse response) {
    TokenResponseDto tokenResponseDto = authService.reissue(refresh);

    // refresh 토큰은 쿠키에 저장
    Cookie cookie = new Cookie("refresh", tokenResponseDto.getRefreshTokenInfoResponse());
    // cookie 설정
    cookie.setMaxAge((int)tokenResponseDto.getRefreshExpireTime());
    cookie.setHttpOnly(true);
    response.addCookie(cookie);

    return ResponseEntity.ok(
        new ApiResponseJson(true, 200, "토큰 재발급에 성공했습니다.", tokenResponseDto.getAccessTokenInfoResponse())
    );
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponseJson> logout(HttpServletRequest request) {
    authService.logout(request.getHeader(JwtFilter.AUTHORIZATION_HEADER));

    return ResponseEntity.ok(
        new ApiResponseJson(true, 200, "로그아웃에 성공했습니다.", null)
    );
  }

}
