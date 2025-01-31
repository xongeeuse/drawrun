package com.dasima.drawrun.domain.user.service;

import com.dasima.drawrun.domain.user.dto.request.RegisterRequestDto;
import com.dasima.drawrun.domain.user.entity.Role;
import com.dasima.drawrun.domain.user.entity.RoleRegister;
import com.dasima.drawrun.domain.user.entity.User;
import com.dasima.drawrun.domain.user.repository.RoleRegisterRepository;
import com.dasima.drawrun.domain.user.repository.RoleRepository;
import com.dasima.drawrun.domain.user.repository.UserRepository;
import com.dasima.drawrun.global.exception.CustomException;
import com.dasima.drawrun.global.exception.ErrorCode;
import com.dasima.drawrun.global.security.dto.response.AccessTokenInfoResponseDto;
import com.dasima.drawrun.global.security.dto.response.TokenResponseDto;
import com.dasima.drawrun.global.security.provider.TokenProvider;
import com.dasima.drawrun.global.util.RedisUtils;
import jakarta.transaction.Transactional;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  @Value("${jwt.secret}")
  private String secret;
  private final TokenProvider tokenProvider;
  private final UserRepository userRepo;
  private final RoleRegisterRepository roleRegisterRepo;
  private final RoleRepository roleRepo;
  private final BCryptPasswordEncoder passwordEncoder;
  private final RedisUtils redisUtils;

  @Override
  public Object register(RegisterRequestDto dto) {

    // 중복 이메일 검사
    if(userRepo.existsByUserEmail(dto.getEmail()))
      throw new CustomException(ErrorCode.DUPLICATE_MEMBER_EMAIL);
    // 중복 아이디 검사
    if(userRepo.existsById(dto.getUserId()))
      throw new CustomException(ErrorCode.DUPLICATE_MEMBER_ID);
    // 중복 닉네임 검사
    if(userRepo.existsByUserNickname(dto.getNickname()))
      throw new CustomException(ErrorCode.DUPLICATE_MEMBER_NICKNAME);

    User user = dto.toUserEntity();

    // password
    user.setUserPassword(passwordEncoder.encode(dto.getPassword()));

    User saveUser = userRepo.save(user);

    Role role = roleRepo.findByRoleId(1); // 1 번 임시사용자.

    // role register
    RoleRegister roleRegister = new RoleRegister(); // 1 번 임시사용자
    roleRegister.setUser(saveUser);
    roleRegister.setRole(role);
    RoleRegister saveRoleRegister = roleRegisterRepo.save(roleRegister);

    return Map.of(
        "status", true
    );
  }

  private User findById(String id) {
    return userRepo.findById(id).orElseThrow(() -> {
      return new CustomException(ErrorCode.NOT_EXIST_MEMBER_ID);
    });
  }

  private User findUserWithRoleNameByUserId(int userId) {
    return userRepo.findUserWithRoleNameById(userId);
  }

  private void checkPassword(String password, User user) {
    if (!passwordEncoder.matches(password, user.getUserPassword())) {
      throw new CustomException(ErrorCode.INCORRECT_MEMBER_PASSWORD);
    }
  }

  @Override
  public TokenResponseDto login(String id, String password) {
    // user 찾기
    User user = findById(id);
    User detailUser = findUserWithRoleNameByUserId(user.getUserId());
    checkPassword(password, detailUser);

    AccessTokenInfoResponseDto accessTokenInfoResponseDto = tokenProvider.createAccessToken(
        detailUser);

    TokenResponseDto tokenResponseDto = new TokenResponseDto();
    tokenResponseDto.setAccessTokenInfoResponse(accessTokenInfoResponseDto);
    tokenResponseDto.setRefreshTokenInfoResponse(
        tokenProvider.createRefreshToken(detailUser, 604800 * 1000));

    // refresh 토큰을 redis에 저장
    redisUtils.setData(tokenResponseDto.getRefreshTokenInfoResponse(), id, (long) 604800 * 1000);
    //refresh 토큰과 access token 두개를 발급한다.
    return tokenResponseDto;
  }

}
