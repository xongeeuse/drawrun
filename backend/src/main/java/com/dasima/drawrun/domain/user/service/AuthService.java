package com.dasima.drawrun.domain.user.service;

import com.dasima.drawrun.domain.user.dto.request.EmailAuthNumberRequestDto;
import com.dasima.drawrun.domain.user.dto.request.EmailSendRequestDto;
import com.dasima.drawrun.domain.user.dto.request.RegisterRequestDto;
import com.dasima.drawrun.domain.user.entity.User;
import com.dasima.drawrun.global.security.dto.response.TokenResponseDto;

public interface AuthService {

  public Object register(RegisterRequestDto dto);

  public TokenResponseDto login(String id, String password);

  public TokenResponseDto reissue(String refreshToken);

  public void logout(String accessToken);

  public void sendmail(EmailSendRequestDto dto);

  public void mailcheck(EmailAuthNumberRequestDto dto);

  public void findPassword(String userId, String email);

  public void changePassword(int userPK, String password, String newPassword);

  public User findId(String email, String username);

  public boolean checkId(String userId);

  public void withdrawAccount(int userPK);

}
