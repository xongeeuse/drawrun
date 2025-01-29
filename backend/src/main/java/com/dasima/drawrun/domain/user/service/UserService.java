package com.dasima.drawrun.domain.user.service;

import com.dasima.drawrun.domain.user.dto.UserDto;
import com.dasima.drawrun.domain.user.dto.request.RegisterRequestDto;
import com.dasima.drawrun.domain.user.entity.User;

public interface UserService {

  public Object register(RegisterRequestDto dto);

}
