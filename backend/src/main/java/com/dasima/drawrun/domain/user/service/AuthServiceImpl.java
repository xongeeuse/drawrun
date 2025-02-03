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
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import java.security.Key;
import java.util.Date;
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
public class AuthServiceImpl implements AuthService {

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
    redisUtils.setData(id, tokenResponseDto.getRefreshTokenInfoResponse(), (long) 604800 * 1000);
    //refresh 토큰과 access token 두개를 발급한다.
    return tokenResponseDto;
  }

  private User findByEmail(String email) {
    return userRepo.findByUserEmail(email).orElseThrow(() -> {
      return new CustomException(ErrorCode.NOT_EXIST_MEMBER_EMAIL);
    });
  }

  @Override
  public TokenResponseDto reissue(String refreshToken) {
    // 토큰 파싱
    byte[] keyBytes = Decoders.BASE64.decode(secret);
    Key hashKey = Keys.hmacShaKeyFor(keyBytes);
    Claims claims = Jwts.parserBuilder().setSigningKey(hashKey).build().parseClaimsJws(refreshToken)
        .getBody();
    String email = claims.getSubject();
    User user = findByEmail(email);
    User detailUser = findUserWithRoleNameByUserId(user.getUserId());
    String userId = user.getId();

    // redis에 refresh 토큰이 존재 하지 않는다면 그냥 검증할 수 없음
    // 위조, 만료, 전부다 막힘
    if (redisUtils.getData(userId) == null) {
      throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
    }
    else {

      // refresh rotate
      // 토큰 재발급

      // access token 재발급
      AccessTokenInfoResponseDto accessTokenInfoResponseDto = tokenProvider.createAccessToken(detailUser);

      // refreshToken의 남은 시간(초)를 계산
      Date expirationDate = claims.getExpiration();
      long currentTimeMillis = System.currentTimeMillis();
      long remainingTimeMillis = expirationDate.getTime() - currentTimeMillis;
      long remainingSeconds = remainingTimeMillis / 1000;

      // refresh token 발급
      TokenResponseDto tokenResponseDto = new TokenResponseDto();
      tokenResponseDto.setRefreshExpireTime(remainingSeconds);
      tokenResponseDto.setAccessTokenInfoResponse(accessTokenInfoResponseDto);
      tokenResponseDto.setRefreshTokenInfoResponse(tokenProvider.createRefreshToken(detailUser, remainingSeconds * 1000));


      // 기존의 refresh token을 삭제
      redisUtils.deleteData(userId);
      redisUtils.setData(userId, tokenResponseDto.getRefreshTokenInfoResponse(),
          remainingSeconds * 1000);
      return tokenResponseDto;
    }
  }

  @Override
  public void logout(String accessToken) {
    byte[] keyBytes = Decoders.BASE64.decode(secret);
    Key hashKey = Keys.hmacShaKeyFor(keyBytes);
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(hashKey)
        .build()
        .parseClaimsJws(accessToken.substring(6))
        .getBody();
    String email = claims.getSubject();
    User user = findByEmail(email);

    redisUtils.deleteData(user.getId());
  }

}
