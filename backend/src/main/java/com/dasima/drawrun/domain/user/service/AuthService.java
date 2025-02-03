package com.dasima.drawrun.domain.user.service;

import com.dasima.drawrun.domain.user.dto.request.RegisterRequestDto;
import com.dasima.drawrun.global.security.dto.response.TokenResponseDto;

public interface AuthService {

  public Object register(RegisterRequestDto dto);

  public TokenResponseDto login(String id, String password);

  public TokenResponseDto reissue(String refreshToken);

  public void logout(String accessToken);

}
